export interface BookingResponse {
    id: string;
    bookedBikeId?: string;
    bikeStationId?: string;
    userId?: string;
    startTime: string; // ISO string
    finishTime: string; // ISO string
    active: boolean;
}

export interface TripResponse {
    id: string;
    bikeId: string;
    userId: string;
    startTime: string; // ISO string
    finishTime?: string; // ISO string
}

export interface UserStatusResponse {
    booking?: BookingResponse;
    trip?: TripResponse;
}