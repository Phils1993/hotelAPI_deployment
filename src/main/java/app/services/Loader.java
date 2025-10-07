package app.services;

import app.daos.HotelDAO;
import app.entities.Hotel;
import app.entities.Room;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class Loader {

    public static void load(EntityManagerFactory emf) {
        HotelDAO hotelDAO = new HotelDAO(emf);

        if (!hotelDAO.getAllHotels().isEmpty()) {
            System.out.println(" Hotels already exist in DB, skipping loader.");
            return;
        }

        // Hotel 1
        Hotel h1 = Hotel.builder()
                .hotelName("Hotel København")
                .hotelAddress("Strøget 1, København")
                .hotelRooms(3)
                .build();

        Room h1r1 = Room.builder().roomNumber(101).price(950.0).hotel(h1).build();
        Room h1r2 = Room.builder().roomNumber(102).price(1050.0).hotel(h1).build();
        Room h1r3 = Room.builder().roomNumber(201).price(1200.0).hotel(h1).build();

        h1.addRoom(h1r1);
        h1.addRoom(h1r2);
        h1.addRoom(h1r3);

        hotelDAO.createHotel(h1);

        // Hotel 2
        Hotel h2 = Hotel.builder()
                .hotelName("Grand Royal Aarhus")
                .hotelAddress("Rådhuspladsen 10, Aarhus")
                .hotelRooms(3)
                .build();

        Room h2r1 = Room.builder().roomNumber(1).price(800.0).hotel(h2).build();
        Room h2r2 = Room.builder().roomNumber(2).price(950.0).hotel(h2).build();
        Room h2r3 = Room.builder().roomNumber(3).price(1100.0).hotel(h2).build();

        h2.addRoom(h2r1);
        h2.addRoom(h2r2);
        h2.addRoom(h2r3);

        hotelDAO.createHotel(h2);

        // Hotel 3
        Hotel h3 = Hotel.builder()
                .hotelName("Seaside Resort Skagen")
                .hotelAddress("Strandvej 7, Skagen")
                .hotelRooms(3)
                .build();

        Room h3r1 = Room.builder().roomNumber(11).price(700.0).hotel(h3).build();
        Room h3r2 = Room.builder().roomNumber(12).price(850.0).hotel(h3).build();
        Room h3r3 = Room.builder().roomNumber(21).price(950.0).hotel(h3).build();

        h3.addRoom(h3r1);
        h3.addRoom(h3r2);
        h3.addRoom(h3r3);

        hotelDAO.createHotel(h3);

        System.out.println("Loader: Tilføjede 3 danske hoteller med rooms til databasen.");
    }
}
