package com.company.enroller.persistence;

import java.util.Collection;

import org.hibernate.Query;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;

@Component("meetingService")
public class MeetingService {

	DatabaseConnector connector;

	public MeetingService() {
		connector = DatabaseConnector.getInstance();
	}

	public Collection<Meeting> getAll() {
		String hql = "FROM Meeting";
		Query query = connector.getSession().createQuery(hql);
		return query.list();
	}

	public Meeting findById(long id) {
		return (Meeting) connector.getSession().get(Meeting.class, id);
	}

	public Meeting saveMeeting(Meeting meeting) {
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().save(meeting);
		transaction.commit();
		return meeting;
	}
	
	public void deleteMeeting(Meeting meeting) {
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().delete(meeting);
		transaction.commit();
	}
	
	public Meeting updateMeeting(Meeting meeting) {
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().merge(meeting);
		transaction.commit();
		return meeting;
	}

	public Collection<Participant> getEnrolled(long id) {
		Meeting meeting = findById(id);
		return meeting.getParticipants();
	}
	
	public Meeting enroll(long id, Participant participant) {
		Transaction transaction = connector.getSession().beginTransaction();
		Meeting meeting = findById(id);
		meeting.addParticipant(participant);
		connector.getSession().merge(meeting);
		transaction.commit();
		return meeting;
	}

	public Meeting unenroll(long id, Participant participant) {
		Transaction transaction = connector.getSession().beginTransaction();
		Meeting meeting = findById(id);
		meeting.removeParticipant(participant);
		connector.getSession().merge(meeting);
		transaction.commit();
		return meeting;
	}

	

	

}
