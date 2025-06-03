import { useMutation, useQueryClient } from "@tanstack/react-query";
import { deactivateBooking } from "../api/bookingApi";
import { UserStateResponse } from "../models/user";

export function useDeactivateBooking() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: deactivateBooking,

    onMutate: async () => {
      await queryClient.cancelQueries({ queryKey: ["userState"] });

      const previousUserState = queryClient.getQueryData<UserStateResponse>(["userState"]);

      // Optimistically remove the booking from user state
      queryClient.setQueryData<UserStateResponse>(["userState"], (old) => {
        if (!old) return old;
        return {
          ...old,
          booking: undefined,
        };
      });

      return { previousUserState };
    },

    onError: (err, _bookingId, context) => {
      if (context?.previousUserState) {
        queryClient.setQueryData(["userState"], context.previousUserState);
      }
      console.error("Failed to cancel booking:", err);
    },

    onSettled: () => {
      void queryClient.invalidateQueries({ queryKey: ["allBikeStations"] });
      void queryClient.invalidateQueries({ queryKey: ["userState"] });
    }
  });
}
