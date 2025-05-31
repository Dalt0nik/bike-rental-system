import { BookingResponse } from "./booking";
import { TripResponse } from "./trip";

export interface UserStateResponse {
  id?: string;
  booking?: BookingResponse;
  trip?: TripResponse;
}

export enum UserStatus {
  Free,
  HasBooking,
  OnTrip,
  Invalid,
}
interface UserStateFree {
  status: UserStatus.Free;
};
interface UserStateHasBooking {
  status: UserStatus.HasBooking;
  booking: BookingResponse;
};
interface UserStateHasTrip {
  status: UserStatus.OnTrip;
  trip: TripResponse;
};
interface UserStateInvalid {
  status: UserStatus.Invalid;
  [key: string]: unknown;
};
export type UserState = UserStateFree | UserStateHasBooking | UserStateHasTrip | UserStateInvalid;

export function deriveUserState(userStateResponse: UserStateResponse): UserState {
  if (userStateResponse.booking !== undefined && userStateResponse.trip !== undefined)
    return { status: UserStatus.Invalid };

  if (userStateResponse.booking !== undefined)
    return { status: UserStatus.HasBooking, booking: userStateResponse.booking };

  if (userStateResponse.trip !== undefined)
    return { status: UserStatus.OnTrip, trip: userStateResponse.trip };

  return { status: UserStatus.Free };
}
