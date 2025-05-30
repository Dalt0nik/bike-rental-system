export interface BookingResponse {
    id: string;
    bookedBikeId?: string;
    bikeStationId?: string;
    userId?: string;
    startTime: string; // ISO string
    finishTime: string; // ISO string
}

export interface CreateBookingRequest {
    bookedBikeId: string;
}