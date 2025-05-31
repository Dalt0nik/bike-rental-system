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
      const previousUserStatus = queryClient.getQueryData<UserStateResponse>(["userState"]);

      const optimisticBooking: BookingResponse = {
        id: "temp-" + Date.now(), // Temporary ID
        bookedBikeId: newBookingRequest.bookedBikeId,
        bikeStationId: newBookingRequest.bikeStationId, // Now we have this!
        userId: undefined, // Server will fill this
        startTime: new Date().toISOString(),
        finishTime: new Date(Date.now() + 20 * 60 * 1000).toISOString(), // 20 minutes from now
      };

      queryClient.setQueryData<UserStateResponse>(["userState"], (old) => ({
        ...old,
        booking: optimisticBooking,
      }));

      return { previousUserStatus };
    },

    onError: (err, _newBookingRequest, context) => {
      if (context?.previousUserStatus) {
        queryClient.setQueryData(["userState"], context.previousUserStatus);
      }
      console.error("Booking failed:", err);
    },

    onSettled: () => {
      queryClient.invalidateQueries({ queryKey: ["userState"] });
      queryClient.invalidateQueries({ queryKey: ["allBikeStations"] });
    },
  });
}
