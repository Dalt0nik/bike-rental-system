import { useEffect, useState } from "react";
import { useDeactivateBooking } from "../hooks/useDeactivateBooking";

export function BookingTimer({ 
  bookingId,
  finishTime, 
  stationAddress 
}: { 
  bookingId: string;
  finishTime: string; 
  stationAddress?: string;
}) {
  const [timeLeft, setTimeLeft] = useState<string>("");
  const deactivateBookingMutation = useDeactivateBooking();

  useEffect(() => {
    const updateTimer = () => {
      const now = new Date().getTime();
      const finish = new Date(finishTime).getTime();
      const diff = finish - now;

      if (diff <= 0) {
        setTimeLeft("Expired");
        return;
      }

      const minutes = Math.floor(diff / (1000 * 60));
      const seconds = Math.floor((diff % (1000 * 60)) / 1000);
      setTimeLeft(`${minutes}:${seconds.toString().padStart(2, "0")}`);
    };

    updateTimer();
    const interval = setInterval(updateTimer, 1000);

    return () => clearInterval(interval);
  }, [finishTime]);

  const handleCancelBooking = () => {
    deactivateBookingMutation.mutate(bookingId);
  };

  if (timeLeft === "Expired")
    return;

  return (
    <div className="absolute top-20 left-1/2 transform -translate-x-1/2 bg-orange-500 text-white px-4 py-2 rounded-lg shadow-lg z-[1000] max-w-[50%] text-center">
      <div className="font-bold">Booking expires in:</div>
      <div className="text-xl font-mono">{timeLeft}</div>
      {stationAddress && (
        <div className="text-sm mt-1">Station: {stationAddress}</div>
      )}
      <button
        onClick={handleCancelBooking}
        disabled={deactivateBookingMutation.isPending}
        className="mt-2 bg-red-600 hover:bg-red-700 disabled:bg-gray-400 text-white px-3 py-1 rounded text-sm font-medium"
      >
        {deactivateBookingMutation.isPending ? "Canceling..." : "Cancel Booking"}
      </button>
    </div>
  );
}