import { BookingResponse } from "./booking";
import { TripResponse } from "./trip";

export interface UserStatusResponse {
    booking?: BookingResponse;
    trip?: TripResponse;
}