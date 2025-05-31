import { useMutation, useQueryClient } from "@tanstack/react-query";
import { startTrip } from "../api/tripApi";
import { UserStateResponse } from "../models/user";
import { CreateTripRequest, TripResponse } from "../models/trip";

export function useStartTrip() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: startTrip,

    onMutate: async (newTripRequest: CreateTripRequest) => {
      // Cancel any outgoing refetches (so they don't overwrite our optimistic update)
      await queryClient.cancelQueries({ queryKey: ["userState"] });

      // Snapshot the previous value (for rollback if needed)
      const previousUserState = queryClient.getQueryData<UserStateResponse>(["userState"]);

      const dateNow = Date.now();
      const tempId = `temp-${dateNow}`; // Temporary ID

      const optimisticTrip: TripResponse = {
        id: tempId,
        bikeId: newTripRequest.bikeId,
        userId: previousUserState?.id ?? tempId,
        startTime: new Date(dateNow).toISOString(),
      };

      queryClient.setQueryData<UserStateResponse>(["userState"], (old) => ({
        id: tempId,
        ...old,
        booking: undefined,
        trip: optimisticTrip,
      }));

      return { previousUserState };
    },

    onError: (err, _newTripRequest, context) => {
      if (context?.previousUserState) {
        queryClient.setQueryData(["userState"], context.previousUserState);
      }
      console.error("Failed to start trip:", err);
    },

    onSettled: async () => {
      await queryClient.invalidateQueries({ queryKey: ["userState"] });
      await queryClient.invalidateQueries({ queryKey: ["allBikeStations"] });
    }
  });
}
