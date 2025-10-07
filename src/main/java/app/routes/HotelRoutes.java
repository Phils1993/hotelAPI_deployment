package app.routes;

import app.Controller.HotelController;
import app.Controller.RoomController;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class HotelRoutes {

    public EndpointGroup getRoutes() {
        return () -> {

            // ========================
            // HOTEL ENDPOINTS
            // ========================
            path("hotel", () -> {
                get(HotelController::getAllHotels);   // GET /hotel
                post(HotelController::createHotel);   // POST /hotel

                path("{hotelId}", () -> {              // /hotel/{hotelId}
                    get(HotelController::getHotelById);
                    put(HotelController::updateHotel);
                    delete(HotelController::deleteHotel);

                    // ========================
                    // ROOMS UNDER SPECIFIC HOTEL
                    // ========================
                    path("rooms", () -> {              // /hotel/{hotelId}/rooms
                        get(HotelController::getRoomsForHotel);   // GET /hotel/{hotelId}/rooms
                        post(RoomController::addRoom);           // POST /hotel/{hotelId}/rooms

                        path("{roomId}", () -> {                 // /hotel/{hotelId}/rooms/{roomId}
                            get(RoomController::getRoomById);   // GET /hotel/{hotelId}/rooms/{roomId}
                            put(RoomController::updateRoom);    // PUT /hotel/{hotelId}/rooms/{roomId}
                            delete(RoomController::deleteRoom); // DELETE /hotel/{hotelId}/rooms/{roomId}
                        });
                    });
                });
            });
        };
    }
}
