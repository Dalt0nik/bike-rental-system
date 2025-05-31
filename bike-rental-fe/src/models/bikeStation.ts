import { BikeResponse } from "./bike";

export interface BikeStationPreviewResponse {
  id: string;
  latitude: number;
  longitude: number;
  capacity: number;
  freeBikes: number;
  freeCapacity: number;
  address?: string;
}

export interface BikeStationResponse {
  id: string;
  latitude: number;
  longitude: number;
  capacity: number;
  bikes: BikeResponse[];
  address?: string;
}
