//*************************************************************************
/**
 * Session.java - A class describing a Session
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

/**
 * This class is an association class between Schedule and Classroom.
 * Note that this implementation does not enforce the candidate key
 * requirement for association class.  Thus the same pair of schedule
 * and classroom may be associated with mulitple sessions, which
 * is really a scheduling conflict.
 * <P>
 * To make implementation consistent with the requirement, one may 
 * have a collection of all sessions and dynamically check for duplication.
 * Readers are encouraged to implement this feature.
 */
public class Session extends NamedObject {

  // Attributes in the Session class
  private Instructor itor1, itor2;  // 1 to 2 instructors

  // Link to course
  private Course course;

  // Links to Schedule and Classroom
  private Schedule schedule;
  private Classroom classroom;

  /** 
   * Constructs a Session object.  It also sets backward links from
   * the schedule and classroom objects for referential integrity.
   *
   * @param name	name of the Session
   * @param course	course of the Session
   * @param schedule	schedule of the Session
   * @param classroom	classroom of the Session
   * @param itor1	first instructor of the Session
   * @param itor2	second instructor of the Session (may be null)
   */
  public Session(String name, Course course,
  		Schedule schedule, Classroom classroom,
		Instructor itor1, Instructor itor2) {
    super(name);
    this.course = course; 
    this.schedule = schedule;
    this.classroom = classroom;

    this.itor1 = itor1;
    this.itor2 = itor2;

    // set links from instructors
    itor1.addSession(this);
    if (itor2 != null) itor2.addSession(this);

    // set backward links for referential integrity
    schedule.addSession(this);
    classroom.addSession(this);
  }

  /** 
   * Returns the course of this Session.
   *
   * @return the course of this Session
   */
  public Course getCourse() { 
    return course; 
  }

  /** 
   * Returns the schedule of this Session.
   *
   * @return the schedule of this Session
   */
  public Schedule getSchedule() {
    return schedule; 
  }

  /** 
   * Returns the classroom of this Session.
   *
   * @return the classroom of this Session
   */
  public Classroom getClassroom() {
    return classroom; 
  }

  /** 
   * Returns the instructors of this Session as an array of two.
   *
   * @return the instructors of this Session
   */
  public Instructor[] getInstructors() {
    Instructor[] itors = {itor1, itor2}; 
    return itors;
  }

  /** 
   * Sets the course of this Session.  Use null to delink from
   * a course.
   *
   * @param the course of this Session
   */
  public void setCourse(Course course) {
    this.course = course;
  }

  /** 
   * Sets the schedule of this Session.  Use null to delink from
   * a schedule.  This core operation gaurantees referential integrity.
   *
   * @param the schedule of this Session
   */
  public void setSchedule(Schedule schedule) {
    try {
      if (schedule == null) this.schedule.removeSession(this); 
    } catch (Exception e) {
      // should not occur
    }
    this.schedule = schedule;
  }

  /** 
   * Sets the classroom of this Session.  Use null to delink from
   * a classroom.  This core operation gaurantees referential integrity.
   *
   * @param the classroom of this Session
   */
  public void setClassroom(Classroom classroom) {
    try {
      if (classroom == null) this.classroom.removeSession(this); 
    } catch (Exception e) {
      // should not occur
    }
    this.classroom = classroom;
  }

  /** 
   * Sets the instructors of this Session.  Use null to delink from
   * an instructor.
   *
   * @param itor1	first instructor of the Session
   * @param itor2	second instructor of the Session
   */
  public void setInstructors(Instructor itor1, Instructor itor2) {
    try {
      if (itor1 == null) this.itor1.removeSession(this); // delink
      if (itor2 == null) this.itor2.removeSession(this);
    } catch (Exception e) {
      // should not occur
    }
    this.itor1 = itor1;
    this.itor2 = itor2;
  }

  /** 
   * Returns the information of this Session as a string.
   * Also lists associated information, but excluding the caller.
   *
   * @param caller the caller of this operation.
   * @return the information of this Session
   */
  public String toString(Object caller) { 
    String lead = "        ";
    String s = lead + "Session: " + super.toString() + "\n";
    if (! (caller instanceof Course) )
      s += lead + "  Course: " + course.getName() + "\n";
    if (! (caller instanceof Schedule) )
      s += lead + "  Schedule: " + schedule.getName() + "\n";
    if (! (caller instanceof Classroom) )
      s += lead + "  Classroom: " + classroom.getName() + "\n";
    if (! (caller instanceof Instructor) ) {
      s += lead + "  Instructor 1: " + itor1.getName() + "\n";
      if (itor2 != null)
        s += lead + "  Instructor 2: " + itor2.getName() + "\n";
    }
    return s;
  }

}

