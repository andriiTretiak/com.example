package com.example;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static com.google.api.services.calendar.CalendarScopes.CALENDAR;
import static com.google.api.services.calendar.CalendarScopes.CALENDAR_EVENTS;

public class GoogleCalendarEventService {

    /** Application name. */
    private static final String APPLICATION_NAME = "EventExtension";
    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    /** Directory to store authorization tokens for this application. */
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = List.of(CALENDAR, CALENDAR_EVENTS);
    private static final String CREDENTIALS_FILE_PATH = "/client_secret_264690784427-1grk1h7ss6mupk7d5d0j9i58jvi4a7tc.apps.googleusercontent.com.json";
    public static final String PRIMARY_CALENDAR_ID = "primary";

    private final Calendar service;

    public GoogleCalendarEventService() throws GeneralSecurityException, IOException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = CalendarQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public Event createEventInPrimaryCalendar(Event event) {
        return createEvent(PRIMARY_CALENDAR_ID, event);
    }

    public Event createEvent(String calendarId, Event event) {
        try {
            return service.events().insert(calendarId, event).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Event getFromPrimaryCalendarBy(String eventId) {
        return getBy(PRIMARY_CALENDAR_ID, eventId);
    }

    public Event getBy(String calendarId, String eventId) {
        try {
            return service.events().get(calendarId, eventId).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Event updateEventFromPrimaryCalendar(Event eventForUpdate) {
        return updateEvent(PRIMARY_CALENDAR_ID, eventForUpdate);
    }

    public Event updateEvent(String calendarId, Event eventForUpdate) {
        try {
            return service.events().patch(calendarId, eventForUpdate.getId(), eventForUpdate).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Event> getEventsListForPrimaryCalendar(Integer maxResults, ZonedDateTime fromTime, OrderedBy orderedBy,
                                                       Boolean isSingleEvents) {
        return getEventsList(PRIMARY_CALENDAR_ID, maxResults, fromTime, orderedBy, isSingleEvents);
    }

    public List<Event> getEventsList(String calendarId, Integer maxResults, ZonedDateTime fromTime, OrderedBy orderedBy,
                                     Boolean isSingleEvents) {
        try {
            Events events = service.events().list(calendarId)
                    .setMaxResults(maxResults)
                    .setTimeMin(fromTime != null ?
                            new DateTime(new Date(fromTime.toEpochSecond()), TimeZone.getTimeZone(fromTime.getZone()))
                            : null)
                    .setOrderBy(orderedBy != null ? orderedBy.getValue() : null)
                    .setSingleEvents(isSingleEvents)
                    .execute();
            return events.getItems();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void deleteEventFromPrimaryCalendar(String eventId) throws IOException {
        deleteEvent(PRIMARY_CALENDAR_ID, eventId);
    }

    public void deleteEvent(String calendarId, String eventId) throws IOException {
        service.events().delete(calendarId, eventId).execute();
    }
}
