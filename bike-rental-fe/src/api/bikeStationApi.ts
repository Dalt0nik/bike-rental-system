import { api } from "./Api";
import { BikeStationResponse } from "../models/bikeStation/bikeStationResponse";

export async function getBikeStations(): Promise<BikeStationResponse[]> {
    const response = await api.get<BikeStationResponse[]>("/bike-stations");
    return response.data;
}
