import { useMutation, useQueryClient } from "@tanstack/react-query";
import { finishTrip } from "../api/tripApi";
import { UserStateResponse } from "../models/user";

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
      console.error("Failed to finish trip:", err);
    },

    onSettled: () => {
      void queryClient.invalidateQueries({ queryKey: ["allBikeStations"] });
      void queryClient.invalidateQueries({ queryKey: ["userState"] });
    }
  });
}
