export enum BikeState {
    FREE,
    BOOKED,
    IN_USE,
    NEEDS_REPAIR
}

export interface BikeResponse {
    id: string;
    curStationId: string;
    bikeState: BikeState;
}
