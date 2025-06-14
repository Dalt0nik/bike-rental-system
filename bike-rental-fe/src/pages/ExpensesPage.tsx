import { useQuery } from "@tanstack/react-query";
import { getAllExpenses } from "../api/expenseApi";
import { ExpenseResponse } from "../models/expense";
import Header from "../components/Header";
import { Calendar, Clock, Bike, KeyRound } from "lucide-react";

export default function ExpensesPage() {
  const { data: expenses = [], isLoading, isError } = useQuery({
    queryKey: ["expenses"],
    queryFn: getAllExpenses
  });

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString();
  };

  const formatTime = (dateString: string) => {
    return new Date(dateString).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  };

  const formatCurrency = (amount: number) => {
    return `€${amount.toFixed(2)}`;
  };

  const calculateTripDuration = (startTime: string, finishTime?: string) => {
    if (!finishTime) return "In progress";
    
    const start = new Date(startTime);
    const end = new Date(finishTime);
    const diffMs = end.getTime() - start.getTime();
    
    const hours = Math.floor(diffMs / (1000 * 60 * 60));
    const minutes = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60));
    
    if (hours > 0) {
      return `${hours}h ${minutes}m`;
    } else {
      return `${minutes}m`;
    }
  };

  const getExpenseType = (expense: ExpenseResponse) => {
    const { tripDTO, bookingDTO, checkDTO } = expense;
    
    // If there's a tripId in checkDTO, it's always a trip (even if it started from a booking)
    if (checkDTO.tripId) {
      if (checkDTO.bookingId) {
        return { type: "trip_with_booking", label: "Trip + Booking" };
      } else {
        return { type: "trip_only", label: "Trip" };
      }
    } else if (checkDTO.bookingId) {
      return { type: "booking_only", label: "Booking" };
    }
    return { type: "unknown", label: "Unknown" };
  };

  const getExpenseIcon = (expense: ExpenseResponse) => {
    const { checkDTO } = expense;
    const isPaid = checkDTO.paid;
    const iconClass = isPaid ? "text-gray-400" : "text-blue-main";
    
    // If there's a tripId, always show bike icon (even if it started from booking)
    if (checkDTO.tripId) {
      return <Bike size={16} className={iconClass} />;
    } else {
      return <KeyRound size={16} className={iconClass} />;
    }
  };

  const totalUnpaid = expenses
    .filter(expense => !expense.checkDTO.paid)
    .reduce((sum, expense) => sum + expense.checkDTO.total, 0);

  if (isLoading) {
    return (
      <div className="fixed inset-0 flex flex-col">
        <Header />
        <div className="flex items-center justify-center flex-1">
          Loading expenses...
        </div>
      </div>
    );
  }

  if (isError) {
    return (
      <div className="fixed inset-0 flex flex-col">
        <Header />
        <div className="flex items-center justify-center flex-1">
          Error loading expenses.
        </div>
      </div>
    );
  }

  return (
    <div className="fixed inset-0 flex flex-col">
      <Header />
      <div className="flex-1 overflow-auto p-4">
        <div className="max-w-4xl mx-auto">
          <h1 className="text-2xl font-bold mb-6">Your Expenses</h1>
          
          {expenses.length === 0 ? (
            <div className="text-center py-8 text-gray-500">
              No expenses found.
            </div>
          ) : (
            <div className="space-y-3">
              {expenses.map((expense) => {
                const expenseType = getExpenseType(expense);
                const { tripDTO, bookingDTO, checkDTO } = expense;
                const isPaid = checkDTO.paid;
                
                return (
                  <div
                    key={checkDTO.id}
                    className={`
                      border rounded-lg p-4 transition-colors
                      ${isPaid 
                        ? 'bg-gray-100 border-gray-300 text-gray-600' 
                        : 'bg-white border-gray-200 hover:border-blue-main'
                      }
                    `}
                  >
                    <div className="flex items-center justify-between">
                      <div className="flex-1">
                        <div className="flex items-center gap-2 mb-2">
                          {getExpenseIcon(expense)}
                          <span className="font-medium">{expenseType.label}</span>
                          {isPaid && (
                            <span className="text-xs bg-gray-200 text-gray-600 px-2 py-1 rounded">
                              PAID
                            </span>
                          )}
                        </div>
                        
                        <div className="space-y-1 text-sm">
                          <div className="flex items-center gap-2">
                            <Calendar size={14} />
                            <span>Date: {formatDate(checkDTO.createdAt)}</span>
                          </div>
                          
                          {/* Show trip info if there's a tripId */}
                          {checkDTO.tripId && tripDTO && (
                            <>
                              <div className="flex items-center gap-2">
                                <Clock size={14} />
                                <span>
                                  Trip: {formatTime(tripDTO.startTime)}
                                  {tripDTO.finishTime && ` - ${formatTime(tripDTO.finishTime)}`}
                                </span>
                              </div>
                              <div className="flex items-center gap-2">
                                <Bike size={14} />
                                <span>Duration: {calculateTripDuration(tripDTO.startTime, tripDTO.finishTime)}</span>
                              </div>
                            </>
                          )}
                          
                          {/* Only show booking info if it's a pure booking (no tripId) */}
                          {!checkDTO.tripId && bookingDTO && (
                            <div className="flex items-center gap-2">
                              <Clock size={14} />
                              <span>
                                Booking: {formatTime(bookingDTO.startTime)} - {formatTime(bookingDTO.finishTime)}
                              </span>
                            </div>
                          )}
                        </div>
                        
                        <div className="mt-2 text-xs space-y-1">
                          {checkDTO.bookingFee > 0 && (
                            <div>Booking fee: {formatCurrency(checkDTO.bookingFee)}</div>
                          )}
                          {checkDTO.unlockFee > 0 && (
                            <div>Unlock fee: {formatCurrency(checkDTO.unlockFee)}</div>
                          )}
                          {checkDTO.tripFee > 0 && (
                            <div>Trip fee: {formatCurrency(checkDTO.tripFee)}</div>
                          )}
                        </div>
                      </div>
                      
                      <div className="text-right">
                        <div className={`text-lg font-bold ${isPaid ? 'text-gray-600' : 'text-blue-main'}`}>
                          {formatCurrency(checkDTO.total)}
                        </div>
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      </div>
      
      {/* Footer with Pay button */}
      {totalUnpaid > 0 && (
        <div className="border-t bg-white p-4 shadow-lg">
          <div className="max-w-4xl mx-auto flex items-center justify-between">
            <div>
              <div className="text-sm text-gray-600">Total unpaid</div>
              <div className="text-xl font-bold text-blue-main">
                {formatCurrency(totalUnpaid)}
              </div>
            </div>
            <button className="bg-blue-main hover:bg-blue-darker text-white px-6 py-2 rounded font-medium transition-colors">
              Pay Now
            </button>
          </div>
        </div>
      )}
    </div>
  );
}