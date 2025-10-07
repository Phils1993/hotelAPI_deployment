package app.daos;

import app.entities.Hotel;
import app.entities.Room;

import java.util.List;

public interface IHotelDAO<T,I> {
    List<Hotel> getAllHotels();
    Hotel getHotelById(int id);
    Hotel createHotel(Hotel hotel);
    Hotel updateHotel(Hotel incomingHotel);
    boolean deleteHotel(int id);

    Hotel addRoom(Hotel hotel, Room room);
    boolean removeRoom(int hotelid, int roomid);
    Room updateRoom(Hotel hotel, Room room);

    Room getRoomById(int id);

    List<Room> getRoomsForHotel(Hotel hotel);

}
