// src/hooks/useCreateBooking.ts
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { createBooking } from "../api/bookingApi";
import { BookingResponse, CreateBookingRequest } from "../models/booking";
import { UserStateResponse } from "../models/user";

// Extended request type that includes station ID for optimistic updates
interface CreateBookingWithStationRequest extends CreateBookingRequest {
  bikeStationId: string;
}

export function useCreateBooking() {
  const queryClient = useQueryClient();

  return useMutation({
    // Only send the booking request part to the API
    mutationFn: (request: CreateBookingWithStationRequest) =>
      createBooking({ bookedBikeId: request.bookedBikeId }),

    onMutate: async (newBookingRequest: CreateBookingWithStationRequest) => {
      // Cancel any outgoing refetches (so they don't overwrite our optimistic update)
      await queryClient.cancelQueries({ queryKey: ["userState"] });

      // Snapshot the previous value (for rollback if needed)
      const previousUserState = queryClient.getQueryData<UserStateResponse>(["userState"]);

      const dateNow = Date.now();
      const tempId = `temp-${dateNow}`; // Temporary ID

      const optimisticBooking: BookingResponse = {
        id: tempId,
        bookedBikeId: newBookingRequest.bookedBikeId,
        bikeStationId: newBookingRequest.bikeStationId, // Now we have this!
        userId: previousUserState?.id ?? tempId,
        startTime: new Date(dateNow).toISOString(),
        finishTime: new Date(dateNow + 20 * 60 * 1000).toISOString(), // 20 minutes from now
      };

      queryClient.setQueryData<UserStateResponse>(["userState"], (old) => ({
        id: tempId,
        ...old,
        booking: optimisticBooking,
      }));

      return { previousUserState };
    },

    onError: (err, _newBookingRequest, context) => {
      if (context?.previousUserState) {
        queryClient.setQueryData(["userState"], context.previousUserState);
      }
      console.error("Booking failed:", err);
    },

    onSettled: async () => {
      await queryClient.invalidateQueries({ queryKey: ["userState"] });
      await queryClient.invalidateQueries({ queryKey: ["allBikeStations"] });
    },
  });
}
