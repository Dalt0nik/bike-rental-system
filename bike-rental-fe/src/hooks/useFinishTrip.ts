import { useMutation, useQueryClient } from "@tanstack/react-query";
import { finishTrip } from "../api/tripApi";
import { toast } from "react-hot-toast";
import { isAxiosError } from "axios";

export function useFinishTrip() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: finishTrip,

    onError: (err) => {
      if (isAxiosError(err) && err.response?.status === 409) {
        toast.error("Bike is not parked", {
          id: "station-error",
        });
      } else {
        toast.error("Failed to finish trip", {
          id: "station-error",
        });
      }

      console.error("Failed to finish trip:", err);
    },

    onSuccess: () => {
      toast.success("Trip is successfully finished");

      void queryClient.invalidateQueries({ queryKey: ["userState"] });
      void queryClient.invalidateQueries({ queryKey: ["allBikeStations"] });
    },
  });
}