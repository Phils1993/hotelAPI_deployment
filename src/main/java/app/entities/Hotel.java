package app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int hotelId;

    @Column(nullable = false)
    private String hotelName;
    @Column(nullable = false)
    private String hotelAddress;
    @Column(nullable = false)
    private int hotelRooms;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "hotel", fetch = FetchType.EAGER,orphanRemoval = true)
    private Set<Room> rooms = new HashSet<>();


    public void addRoom(Room room){
       if(rooms == null) {
           rooms = new HashSet<>();
       }
       rooms.add(room);
       room.setHotel(this);
    }

}
