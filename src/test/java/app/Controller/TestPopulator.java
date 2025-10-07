package app.Controller;

import app.daos.HotelDAO;
import app.entities.Hotel;
import app.entities.Room;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class TestPopulator {

    public static void populate(EntityManagerFactory emf) {
        HotelDAO dao = new HotelDAO(emf);

        // --- Slet eksisterende data ---
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.getTransaction().commit();
        }

        // --- Opret testhoteller med danske navne og adresser ---
        Hotel h1 = Hotel.builder()
                .hotelName("Hotel København")
                .hotelAddress("Strøget 1, København")
                .hotelRooms(2)
                .build();

        Room h1r1 = Room.builder().roomNumber(101).price(950.0).hotel(h1).build();
        Room h1r2 = Room.builder().roomNumber(102).price(1050.0).hotel(h1).build();
        h1.addRoom(h1r1);
        h1.addRoom(h1r2);

        Hotel h2 = Hotel.builder()
                .hotelName("Hotel Aarhus")
                .hotelAddress("Rådhuspladsen 2, Aarhus")
                .hotelRooms(2)
                .build();

        Room h2r1 = Room.builder().roomNumber(201).price(800.0).hotel(h2).build();
        Room h2r2 = Room.builder().roomNumber(202).price(900.0).hotel(h2).build();
        h2.addRoom(h2r1);
        h2.addRoom(h2r2);

        Hotel h3 = Hotel.builder()
                .hotelName("Hotel Odense")
                .hotelAddress("Fynsvej 3, Odense")
                .hotelRooms(1)
                .build();

        Room h3r1 = Room.builder().roomNumber(301).price(700.0).hotel(h3).build();
        h3.addRoom(h3r1);

        List<Hotel> hotels = List.of(h1, h2, h3);
        hotels.forEach(dao::createHotel);

        System.out.println("TestPopulator: Indsatte " + hotels.size() + " danske testhoteller med rooms i databasen.");
    }
}

