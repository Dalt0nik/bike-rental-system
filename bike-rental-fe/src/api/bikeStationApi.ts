import { api } from "./Api";
import { BikeStationPreviewResponse } from "../models/bikeStation";

export async function getAllBikeStations(): Promise<BikeStationPreviewResponse[]> {
    return (await api.get<BikeStationPreviewResponse[]>("/bike-stations")).data;
}

export async function getBikeStation(id: string): Promise<BikeStationPreviewResponse[]> {
    return (await api.get<BikeStationPreviewResponse[]>(`/bike-stations/${id}`)).data;
}