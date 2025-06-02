import { BookingResponse, CreateBookingRequest } from "../models/booking";
import { api } from "./Api";

export async function createBooking(request: CreateBookingRequest): Promise<BookingResponse> {
  return (await api.post<BookingResponse>("/bookings", request)).data;
}

export async function deactivateBooking(bookingId: string): Promise<void> {
  await api.patch(`/bookings/${bookingId}/deactivate`);
}
