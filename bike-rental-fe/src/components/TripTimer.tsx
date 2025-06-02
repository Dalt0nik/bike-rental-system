import { Bike, Square } from "lucide-react";
import { useEffect, useState } from "react";
import { useFinishTrip } from "../hooks/useFinishTrip";

interface TripTimerProps {
  tripId: string;
  startTime: string;
}

export function TripTimer({ tripId, startTime }: TripTimerProps) {
  const [timeElapsed, setTimeElapsed] = useState<string>("");
  const finishTripMutation = useFinishTrip();

  useEffect(() => {
    const updateTimer = () => {
      const now = new Date().getTime();
      const start = new Date(startTime).getTime();
      const diff = now - start;

      const hours = Math.floor(diff / (1000 * 60 * 60));
      const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
      const seconds = Math.floor((diff % (1000 * 60)) / 1000);

      if (hours > 0) {
        setTimeElapsed(`${hours}:${minutes.toString().padStart(2, "0")}:${seconds.toString().padStart(2, "0")}`);
      } else {
        setTimeElapsed(`${minutes}:${seconds.toString().padStart(2, "0")}`);
      }
    };

    updateTimer();
    const interval = setInterval(updateTimer, 1000);

    return () => clearInterval(interval);
  }, [startTime]);

  const handleFinishTrip = () => {
    finishTripMutation.mutate(tripId);
  };

  return (
    <div className="absolute top-20 left-1/2 transform -translate-x-1/2 bg-blue-main text-white px-4 py-2 rounded-lg shadow-lg z-[1000] max-w-[50%] text-center">
      <div className="font-bold">Time traveled:</div>
      <div className="text-xl font-mono">{timeElapsed}</div>
      <div className="text-sm mt-1 flex items-center justify-center gap-1">
        <Bike size={14} />
        Trip in progress
      </div>
      <button
        onClick={handleFinishTrip}
        disabled={finishTripMutation.isPending}
        className="mt-2 bg-orange-dark hover:bg-red-700 disabled:bg-gray-400 text-white px-3 py-1 rounded text-sm font-medium transition-colors flex items-center gap-1 mx-auto"
      >
        <Square size={12} />
        {finishTripMutation.isPending ? "Finishing..." : "Finish Trip"}
      </button>
    </div>
  );
}