import { BookingResponse } from "./booking";
import { TripResponse } from "./trip";

export interface UserStateResponse {
  id?: string;
  booking?: BookingResponse;
  trip?: TripResponse;
}

export enum UserStatus {
  FREE,
  HAS_BOOKING,
  ON_TRIP,
  INVALID,
}
interface UserStateFree {
  status: UserStatus.FREE;
};
interface UserStateHasBooking {
  status: UserStatus.HAS_BOOKING;
  booking: BookingResponse;
};
interface UserStateHasTrip {
  status: UserStatus.ON_TRIP;
  trip: TripResponse;
};
interface UserStateInvalid {
  status: UserStatus.INVALID;
  [key: string]: unknown;
};
export type UserState = UserStateFree | UserStateHasBooking | UserStateHasTrip | UserStateInvalid;

export function deriveUserState(userStateResponse: UserStateResponse): UserState {
  if (userStateResponse.booking !== undefined && userStateResponse.trip !== undefined)
    return { status: UserStatus.INVALID, ...userStateResponse };

  if (userStateResponse.booking !== undefined)
    return { status: UserStatus.HAS_BOOKING, booking: userStateResponse.booking };

  if (userStateResponse.trip !== undefined)
    return { status: UserStatus.ON_TRIP, trip: userStateResponse.trip };

  return { status: UserStatus.FREE };
}
