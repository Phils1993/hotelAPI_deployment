package app.dtos;


import app.entities.Room;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomDTO {
    private int id;
    private int hotelId;
    private int roomNumber;
    private double price;

    // FIXME : Dette burde være i en mapper klasse!!
    public RoomDTO(Room room) {
        this.id = room.getId();
        this.roomNumber = room.getRoomNumber();
        this.price = room.getPrice();
        this.hotelId = room.getHotel().getHotelId(); // get ID from Hotel entity
    }

    // FIXME : Dette burde være i en mapper klasse!!
    public RoomDTO(RoomDTO roomDTO) {
        this.id = roomDTO.getId();
        this.roomNumber = roomDTO.getRoomNumber();
        this.price = roomDTO.getPrice();
        this.hotelId = roomDTO.getHotelId();
    }
}
