export interface TripResponse {
  id: string;
  bikeId: string;
  userId: string;
  startTime: string; // ISO string
  finishTime?: string; // ISO string
}

export interface CreateTripRequest {
  bikeId: string;
}
