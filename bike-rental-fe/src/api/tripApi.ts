import { CreateTripRequest, TripResponse } from "../models/trip";
import { api } from "./Api";

export async function startTrip(request: CreateTripRequest): Promise<TripResponse> {
  return (await api.post<TripResponse>("/trips", request)).data;
}
