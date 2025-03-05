package services;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Collections;

public class GoogleMeetAPI {

    public static String createMeetLink(ZonedDateTime startDateTime, ZonedDateTime endDateTime) throws IOException, GeneralSecurityException {
        Calendar service = GoogleOAuth.getCalendarService();

        // Définir l'heure de début et de fin au format ISO
        EventDateTime start = new EventDateTime().setDateTime(new DateTime(startDateTime.toInstant().toEpochMilli()))
                .setTimeZone("UTC");  // Remplace "UTC" par le fuseau horaire nécessaire

        EventDateTime end = new EventDateTime().setDateTime(new DateTime(endDateTime.toInstant().toEpochMilli()))
                .setTimeZone("UTC");

        // Ajouter la conférence Google Meet
        ConferenceData conferenceData = new ConferenceData();
        conferenceData.setCreateRequest(new CreateConferenceRequest()
                .setRequestId("meet-" + Instant.now().toEpochMilli())
                .setConferenceSolutionKey(new ConferenceSolutionKey().setType("hangoutsMeet"))); // ✅ Correction ici

        // Créer l'événement
        Event event = new Event()
                .setSummary("Entretien en ligne")
                .setStart(start)
                .setEnd(end)
                .setConferenceData(conferenceData)
                .setAttendees(Collections.singletonList(new EventAttendee().setEmail("gaithhmidi16@gmail.com"))); // Remplace par un vrai email

        // Insérer l'événement dans Google Calendar
        Event createdEvent = service.events().insert("primary", event)
                .setConferenceDataVersion(1)
                .execute();

        // Récupérer le lien Meet
        if (createdEvent.getConferenceData() != null && createdEvent.getConferenceData().getEntryPoints() != null) {
            for (EntryPoint entryPoint : createdEvent.getConferenceData().getEntryPoints()) {
                if ("video".equals(entryPoint.getEntryPointType())) {
                    return entryPoint.getUri(); // ✅ Lien Google Meet
                }
            }
        }
        return "Lien Meet non disponible";
    }
}
