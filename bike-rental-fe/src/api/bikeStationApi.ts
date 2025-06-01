import { api } from "./Api";
import { BikeStationPreviewResponse, BikeStationResponse } from "../models/bikeStation";

export async function getAllBikeStations(): Promise<BikeStationPreviewResponse[]> {
  return (await api.get<BikeStationPreviewResponse[]>("/bike-stations")).data;
}

export async function getBikeStation(id: string): Promise<BikeStationResponse> {
  return (await api.get<BikeStationResponse>(`/bike-stations/${id}`)).data;
}
