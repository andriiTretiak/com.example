package com.example;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class CalendarQuickstart {

    public static void main(String... args) throws IOException, GeneralSecurityException {
        GoogleCalendarEventService eventService = new GoogleCalendarEventService();


        // create new event
        Event result = eventService.createEventInPrimaryCalendar(createEvent());

        // get event by id
        Event eventFromCalendar = eventService.getFromPrimaryCalendarBy(result.getId());

        // update event
        Event updatedEvent = eventService.updateEventFromPrimaryCalendar(eventFromCalendar.setSummary("Updated event"));

        printListEvents(eventService.getEventsListForPrimaryCalendar(10, ZonedDateTime.now(),
                OrderedBy.START_TIME, true));

        eventService.deleteEventFromPrimaryCalendar(updatedEvent.getId());

        printListEvents(eventService.getEventsListForPrimaryCalendar(10, ZonedDateTime.now(),
                OrderedBy.START_TIME, true));
    }

    private static void printListEvents(List<Event> items) {
        if (items.isEmpty()) {
            System.out.println("No upcoming events found.");
        } else {
            System.out.println("Upcoming events");
            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                }
                System.out.printf("%s (%s)\n", event.getSummary(), start);
            }
        }
    }

    private static Event createEvent() {
        Event event = new Event();
        event.setSummary("New Event");
        Date startDate = new Date();
        Date endDate = new Date(startDate.getTime() + 3600000);
        DateTime start = new DateTime(startDate, TimeZone.getTimeZone("UTC"));
        event.setStart(new EventDateTime().setDateTime(start));
        DateTime end = new DateTime(endDate, TimeZone.getTimeZone("UTC"));
        event.setEnd(new EventDateTime().setDateTime(end));
//        event.setAttendees(List.of(new EventAttendee().setEmail("cpdscabellos@gmail.com")));
        return event;
    }
}