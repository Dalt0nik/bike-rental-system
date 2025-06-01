import { useEffect, useState } from "react";

export function TripTimer({ startTime }: { startTime: string }) {
  const [timeElapsed, setTimeElapsed] = useState<string>("");

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

  return (
    <div className="absolute top-20 left-1/2 transform -translate-x-1/2 bg-blue-500 text-white px-4 py-2 rounded-lg shadow-lg z-[1000] max-w-[50%] text-center">
      <div className="font-bold">Time traveled:</div>
      <div className="text-xl font-mono">{timeElapsed}</div>
      <div className="text-sm mt-1">🚴 Trip in progress</div>
    </div>
  );
}
