import { api } from "./Api";
import { UserStatusResponse } from "../models/user";

export async function getUserStatus(): Promise<UserStatusResponse> {
    return (await api.get<UserStatusResponse>("/users/status")).data;
}