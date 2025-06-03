import { api } from "./Api";
import { ExpenseResponse } from "../models/expense";

export async function getAllExpenses(): Promise<ExpenseResponse[]> {
  return (await api.get<ExpenseResponse[]>("check/expenses")).data;
}