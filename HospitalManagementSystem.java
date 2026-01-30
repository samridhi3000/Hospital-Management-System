import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.io.IOException;

// Patient class
class Patient {
    private String id;
    private String name;
    private int age;
    private String contact;

    public Patient(String id, String name, int age, String contact) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.contact = contact;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    @Override
    public String toString() {
        return "ID: " + id + ", Name: " + name + ", Age: " + age + ", Contact: " + contact;
    }
}

// Doctor class
class Doctor {
    private String id;
    private String name;
    private String specialty;

    public Doctor(String id, String name, String specialty) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    @Override
    public String toString() {
        return "ID: " + id + ", Name: " + name + ", Specialty: " + specialty;
    }
}

// Appointment class
class Appointment {
    private String id;
    private Patient patient;
    private Doctor doctor;
    private LocalDateTime dateTime;
    private String notes;

    public Appointment(String id, Patient patient, Doctor doctor, LocalDateTime dateTime, String notes) {
        this.id = id;
        this.patient = patient;
        this.doctor = doctor;
        this.dateTime = dateTime;
        this.notes = notes;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Patient getPatient() { return patient; }
    public Doctor getDoctor() { return doctor; }
    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return "ID: " + id + ", Patient: " + patient.getName() + ", Doctor: " + doctor.getName() + ", DateTime: " + dateTime.format(formatter) + ", Notes: " + notes;
    }
}

// Main Hospital Management System class with GUI
public class HospitalManagementSystem extends JFrame {
    private List<Patient> patients = new ArrayList<>();
    private Map<String, Doctor> doctors = new HashMap<>();
    private List<Appointment> appointments = new ArrayList<>();

    private JTable patientTable, doctorTable, appointmentTable;
    private DefaultTableModel patientModel, doctorModel, appointmentModel;

    public HospitalManagementSystem() {
        setTitle("Hospital Patient Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Tabs for different sections
        JTabbedPane tabbedPane = new JTabbedPane();

        // Patient Management Tab
        JPanel patientPanel = new JPanel(new BorderLayout());
        patientModel = new DefaultTableModel(new String[]{"ID", "Name", "Age", "Contact"}, 0);
        patientTable = new JTable(patientModel);
        JScrollPane patientScroll = new JScrollPane(patientTable);
        patientPanel.add(patientScroll, BorderLayout.CENTER);

        JPanel patientButtons = new JPanel();
        JButton addPatientBtn = new JButton("Add Patient");
        JButton updatePatientBtn = new JButton("Update Patient");
        JButton deletePatientBtn = new JButton("Delete Patient");
        patientButtons.add(addPatientBtn);
        patientButtons.add(updatePatientBtn);
        patientButtons.add(deletePatientBtn);
        patientPanel.add(patientButtons, BorderLayout.SOUTH);

        tabbedPane.addTab("Patients", patientPanel);

        // Doctor Management Tab
        JPanel doctorPanel = new JPanel(new BorderLayout());
        doctorModel = new DefaultTableModel(new String[]{"ID", "Name", "Specialty"}, 0);
        doctorTable = new JTable(doctorModel);
        JScrollPane doctorScroll = new JScrollPane(doctorTable);
        doctorPanel.add(doctorScroll, BorderLayout.CENTER);

        JPanel doctorButtons = new JPanel();
        JButton addDoctorBtn = new JButton("Add Doctor");
        JButton updateDoctorBtn = new JButton("Update Doctor");
        JButton deleteDoctorBtn = new JButton("Delete Doctor");
        doctorButtons.add(addDoctorBtn);
        doctorButtons.add(updateDoctorBtn);
        doctorButtons.add(deleteDoctorBtn);
        doctorPanel.add(doctorButtons, BorderLayout.SOUTH);

        tabbedPane.addTab("Doctors", doctorPanel);

        // Appointment Management Tab
        JPanel appointmentPanel = new JPanel(new BorderLayout());
        appointmentModel = new DefaultTableModel(new String[]{"ID", "Patient", "Doctor", "DateTime", "Notes"}, 0);
        appointmentTable = new JTable(appointmentModel);
        JScrollPane appointmentScroll = new JScrollPane(appointmentTable);
        appointmentPanel.add(appointmentScroll, BorderLayout.CENTER);

        JPanel appointmentButtons = new JPanel();
        JButton bookAppointmentBtn = new JButton("Book Appointment");
        JButton cancelAppointmentBtn = new JButton("Cancel Appointment");
        JButton printAppointmentBtn = new JButton("Print Appointment");  // New print button

        appointmentButtons.add(bookAppointmentBtn);
        appointmentButtons.add(cancelAppointmentBtn);
        appointmentButtons.add(printAppointmentBtn);
        appointmentPanel.add(appointmentButtons, BorderLayout.SOUTH);

        tabbedPane.addTab("Appointments", appointmentPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Event Listeners
        addPatientBtn.addActionListener(e -> addPatient());
        updatePatientBtn.addActionListener(e -> updatePatient());
        deletePatientBtn.addActionListener(e -> deletePatient());

        addDoctorBtn.addActionListener(e -> addDoctor());
        updateDoctorBtn.addActionListener(e -> updateDoctor());
        deleteDoctorBtn.addActionListener(e -> deleteDoctor());

        bookAppointmentBtn.addActionListener(e -> bookAppointment());
        cancelAppointmentBtn.addActionListener(e -> cancelAppointment());

        printAppointmentBtn.addActionListener(e -> printAppointment());  // Listener for print button

        refreshTables();
    }

    // Patient methods
    private void addPatient() {
        try {
            String id = JOptionPane.showInputDialog("Enter Patient ID:");
            String name = JOptionPane.showInputDialog("Enter Patient Name:");
            int age = Integer.parseInt(JOptionPane.showInputDialog("Enter Patient Age:"));
            String contact = JOptionPane.showInputDialog("Enter Patient Contact:");
            patients.add(new Patient(id, name, age, contact));
            refreshTables();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid age input.");
        }
    }

    private void updatePatient() {
        int row = patientTable.getSelectedRow();
        if (row >= 0) {
            Patient p = patients.get(row);
            p.setName(JOptionPane.showInputDialog("Update Name:", p.getName()));
            try {
                p.setAge(Integer.parseInt(JOptionPane.showInputDialog("Update Age:", p.getAge())));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid age.");
            }
            p.setContact(JOptionPane.showInputDialog("Update Contact:", p.getContact()));
            refreshTables();
        } else {
            JOptionPane.showMessageDialog(this, "Select a patient to update.");
        }
    }

    private void deletePatient() {
        int row = patientTable.getSelectedRow();
        if (row >= 0) {
            patients.remove(row);
            refreshTables();
        } else {
            JOptionPane.showMessageDialog(this, "Select a patient to delete.");
        }
    }

    // Doctor methods
    private void addDoctor() {
        String id = JOptionPane.showInputDialog("Enter Doctor ID:");
        String name = JOptionPane.showInputDialog("Enter Doctor Name:");
        String specialty = JOptionPane.showInputDialog("Enter Doctor Specialty:");
        doctors.put(id, new Doctor(id, name, specialty));
        refreshTables();
    }

    private void updateDoctor() {
        String id = JOptionPane.showInputDialog("Enter Doctor ID to update:");
        Doctor d = doctors.get(id);
        if (d != null) {
            d.setName(JOptionPane.showInputDialog("Update Name:", d.getName()));
            d.setSpecialty(JOptionPane.showInputDialog("Update Specialty:", d.getSpecialty()));
            refreshTables();
        } else {
            JOptionPane.showMessageDialog(this, "Doctor not found.");
        }
    }

    private void deleteDoctor() {
        String id = JOptionPane.showInputDialog("Enter Doctor ID to delete:");
        doctors.remove(id);
        refreshTables();
    }

    // Appointment methods
    private void bookAppointment() {
        try {
            String id = JOptionPane.showInputDialog("Enter Appointment ID:");
            String patientId = JOptionPane.showInputDialog("Enter Patient ID:");
            String doctorId = JOptionPane.showInputDialog("Enter Doctor ID:");
            String dateTimeStr = JOptionPane.showInputDialog("Enter DateTime (yyyy-MM-dd HH:mm):");
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            String notes = JOptionPane.showInputDialog("Enter Notes (e.g., prescription):");

            Patient patient = patients.stream().filter(p -> p.getId().equals(patientId)).findFirst().orElse(null);
            Doctor doctor = doctors.get(doctorId);

            if (patient != null && doctor != null) {
                appointments.add(new Appointment(id, patient, doctor, dateTime, notes));
                refreshTables();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid patient or doctor ID.");
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date/time format.");
        }
    }

    private void cancelAppointment() {
        int row = appointmentTable.getSelectedRow();
        if (row >= 0) {
            appointments.remove(row);
            refreshTables();
        } else {
            JOptionPane.showMessageDialog(this, "Select an appointment to cancel.");
        }
    }

    // Print appointment as PDF
    private void printAppointment() {
        int row = appointmentTable.getSelectedRow();
        if (row >= 0) {
            Appointment appointment = appointments.get(row);
            try {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream("appointment_" + appointment.getId() + ".pdf"));
                document.open();
                document.add(new Paragraph("Appointment Details"));
                document.add(new Paragraph("ID: " + appointment.getId()));
                document.add(new Paragraph("Patient: " + appointment.getPatient().getName()));
                document.add(new Paragraph("Doctor: " + appointment.getDoctor().getName()));
                document.add(new Paragraph("Date & Time: " + appointment.getDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
                document.add(new Paragraph("Notes: " + appointment.getNotes()));
                document.close();
                JOptionPane.showMessageDialog(this, "Appointment printed as PDF.");
            } catch (DocumentException | IOException e) {
                JOptionPane.showMessageDialog(this, "Error generating PDF: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Select an appointment to print.");
        }
    }

    // Refresh tables
    private void refreshTables() {
        patientModel.setRowCount(0);
        for (Patient p : patients) {
            patientModel.addRow(new Object[]{p.getId(), p.getName(), p.getAge(), p.getContact()});
        }

        doctorModel.setRowCount(0);
        for (Doctor d : doctors.values()) {
            doctorModel.addRow(new Object[]{d.getId(), d.getName(), d.getSpecialty()});
        }

        appointmentModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Appointment a : appointments) {
            appointmentModel.addRow(new Object[]{a.getId(), a.getPatient().getName(), a.getDoctor().getName(), a.getDateTime().format(formatter), a.getNotes()});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HospitalManagementSystem().setVisible(true);
        });
    }
}
