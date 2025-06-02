import { useMutation, useQueryClient } from "@tanstack/react-query";
import { finishTrip } from "../api/tripApi";
import { UserStateResponse } from "../models/user";
import { toast } from "react-hot-toast";
import { isAxiosError } from "axios";

export function useFinishTrip() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: finishTrip,

    onMutate: async () => {
      // Cancel any outgoing refetches (so they don't overwrite our optimistic update)
      await queryClient.cancelQueries({ queryKey: ["userState"] });

      // Snapshot the previous value (for rollback if needed)
      const previousUserState = queryClient.getQueryData<UserStateResponse>(["userState"]);

      // Optimistically remove the trip from user state
      queryClient.setQueryData<UserStateResponse>(["userState"], (old) => {
        if (!old) return old;
        return {
          ...old,
          trip: undefined,
        };
      });

      return { previousUserState };
    },

    onError: (err, _tripId, context) => {
      if (context?.previousUserState) {
        queryClient.setQueryData(["userState"], context.previousUserState);
      }

      if (isAxiosError(err) && err.response?.status === 409) {
        toast.error("Bike is not parked", {
          id: "station-error",
        });
      } else {
        toast.error("Failed to finish trip", {
          id: "station-error",
        });
      }

      console.error("Failed to finish trip:", err);
    },

    onSuccess: () => {
      toast.success("Trip is successfully finished");
    },

    onSettled: () => {
      void queryClient.invalidateQueries({ queryKey: ["allBikeStations"] });
      void queryClient.invalidateQueries({ queryKey: ["userState"] });
    }
  });
}
