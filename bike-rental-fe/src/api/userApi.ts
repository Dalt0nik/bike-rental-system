import { api } from "./Api";
import { UserStateResponse } from "../models/user";

export async function getUserState(): Promise<UserStateResponse> {
  return (await api.get<UserStateResponse>("/users/status")).data;
}
