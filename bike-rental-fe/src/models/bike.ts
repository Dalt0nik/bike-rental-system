export enum BikeState {
    FREE = "FREE",
    BOOKED = "BOOKED", 
    IN_USE = "IN_USE",
    NEEDS_REPAIR = "NEEDS_REPAIR"
}

export interface BikeResponse {
    id: string;
    curStationId: string;
    state: BikeState;
}
