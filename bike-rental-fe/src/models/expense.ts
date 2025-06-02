import { BookingResponse } from "./booking";
import { TripResponse } from "./trip";

export interface CheckResponse {
  id: string;
  userId: string;
  bookingId?: string;
  tripId?: string;
  bookingFee: number;
  unlockFee: number;
  tripFee: number;
  total: number;
  paid: boolean;
  createdAt: string; // ISO string
}

export interface ExpenseResponse {
  bookingDTO?: BookingResponse;
  tripDTO?: TripResponse;
  checkDTO: CheckResponse;
}