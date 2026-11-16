import os
import docx
from docx import Document
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_ALIGN_VERTICAL
from report_utils import (
    add_para, add_heading_1, add_heading_2, add_heading_3,
    add_figure, set_cell_background, set_cell_margins, set_table_borders,
    format_table_header, format_table_row
)

def create_full_report():
    doc = Document()

    # Page setup: A4 with 1.0 inch margins
    for sec in doc.sections:
        sec.top_margin = Inches(1.0)
        sec.bottom_margin = Inches(1.0)
        sec.left_margin = Inches(1.0)
        sec.right_margin = Inches(1.0)
        sec.page_width = Inches(8.27)
        sec.page_height = Inches(11.69)

    # =========================================================================
    # PAGE 1: COVER PAGE / TITLE PAGE (PRESERVED TEMPLATE STRUCTURE)
    # =========================================================================
    add_para(doc, "(A typical Specimen of Cover Page & Title Page)", font_size=11, italic=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=4)
    add_para(doc, "<Font Style Times New Roman - Bold>", font_size=10, italic=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=12)

    add_para(doc, "SUPPLYSYNC – ENTERPRISE SUPPLY CHAIN AND VENDOR MANAGEMENT SYSTEM", font_size=18, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=4, line_spacing=1.3, color=RGBColor(0x0F, 0x17, 0x2A))
    add_para(doc, "<Font Size 18> <1.5 line spacing>", font_size=10, italic=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=10)

    add_para(doc, "21CSP401L / 21CSP402L - Major Project", font_size=14, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=2)
    add_para(doc, "<Font Size 14>", font_size=10, italic=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=14)

    add_para(doc, "Submitted by", font_size=13, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=2)
    add_para(doc, "<Font Size 14> <Italic>", font_size=10, italic=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=6)
    add_para(doc, "STUDENT1 NAME [REG NUM]", font_size=14, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=2)
    add_para(doc, "STUDENT2 NAME [REG NUM]", font_size=14, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=2)
    add_para(doc, "<Font Size 16>", font_size=10, italic=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=14)

    add_para(doc, "Under the Guidance of", font_size=13, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=2)
    add_para(doc, "<Font Size 14> <Italic>", font_size=10, italic=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=6)
    add_para(doc, "(GUIDE NAME )", font_size=14, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=2)
    add_para(doc, "<Font Size 16>", font_size=10, italic=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=2)
    add_para(doc, "(Designation, Department)", font_size=12, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=2)
    add_para(doc, "<Font Size 12>", font_size=10, italic=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=16)

    add_para(doc, "in partial fulfillment of the requirements for the degree of", font_size=12, italic=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=2)
    add_para(doc, "<Font Size 14> <1.5 line spacing>", font_size=10, italic=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=8)
    add_para(doc, "BACHELOR OF TECHNOLOGY", font_size=14, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=2)
    add_para(doc, "in", font_size=12, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=2)
    add_para(doc, "COMPUTER SCIENCE ENGINEERING", font_size=14, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=2)
    add_para(doc, "with specialization in (SPECIALIZATION NAME)", font_size=12, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=10)

    # Logo
    if os.path.exists("report_assets/extracted_ref/image1.jpeg"):
        p_logo = doc.add_paragraph()
        p_logo.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_logo.paragraph_format.space_before = Pt(4)
        p_logo.paragraph_format.space_after = Pt(8)
        run_l = p_logo.add_run()
        run_l.add_picture("report_assets/extracted_ref/image1.jpeg", width=Inches(3.8))

    add_para(doc, "<Font Size 16>", font_size=10, italic=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=4)
    add_para(doc, "DEPARTMENT OF <students department>", font_size=13, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=2)
    add_para(doc, "COLLEGE OF ENGINEERING AND TECHNOLOGY", font_size=13, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=2)
    add_para(doc, "SRM INSTITUTE OF SCIENCE AND TECHNOLOGY", font_size=14, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=2)
    add_para(doc, "KATTANKULATHUR- 603 203", font_size=13, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=6)
    add_para(doc, "<Font Size 16><l.5 line spacing>", font_size=10, italic=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=4)
    add_para(doc, "MAY  2026", font_size=13, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=2)
    add_para(doc, "<Font Size 14>", font_size=10, italic=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=0)

    doc.add_page_break()

    # =========================================================================
    # PAGE 2: OWN WORK DECLARATION FORM (PRESERVED TEMPLATE STRUCTURE)
    # =========================================================================
    if os.path.exists("report_assets/extracted_ref/image2.jpeg"):
        p_logo2 = doc.add_paragraph()
        p_logo2.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_logo2.paragraph_format.space_before = Pt(0)
        p_logo2.paragraph_format.space_after = Pt(4)
        run_l2 = p_logo2.add_run()
        run_l2.add_picture("report_assets/extracted_ref/image2.jpeg", width=Inches(2.5))

    add_para(doc, "Department of  <Students department >", font_size=12, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=2)
    add_para(doc, "SRM Institute of Science & Technology", font_size=13, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=2)
    add_para(doc, "Own Work Declaration Form", font_size=14, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=12)

    add_para(doc, "This sheet must be filled in (each box ticked to show that the condition has been met). It must be signed and dated along with your student registration number and included with all assignments you submit – work will not be marked unless this is done.", font_size=10.5, italic=True, space_after=10)

    add_para(doc, "To be completed by the student for all assessments:", font_size=11, bold=True, space_after=6)

    # Details table
    dtbl = doc.add_table(rows=4, cols=2)
    dtbl.alignment = WD_TABLE_ALIGNMENT.CENTER
    dtbl_widths = [2.2, 4.0]
    dtbl_rows = [
        ("Degree/ Course", ":  B.Tech / 21CSP401L Major Project"),
        ("Student Name", ":  STUDENT1 NAME & STUDENT2 NAME"),
        ("Registration Number", ":  [REGISTRATION NUMBERS]"),
        ("Title of Work", ":  SupplySync – Enterprise Supply Chain and Vendor Management System")
    ]
    for r_idx, (k, v) in enumerate(dtbl_rows):
        r = dtbl.rows[r_idx]
        for c_idx, cell in enumerate(r.cells):
            cell.width = Inches(dtbl_widths[c_idx])
            set_cell_margins(cell, top=40, bottom=40, left=60, right=60)
            p = cell.paragraphs[0]
            p.paragraph_format.space_before = Pt(1)
            p.paragraph_format.space_after = Pt(1)
            run = p.add_run(k if c_idx == 0 else v)
            run.font.name = "Times New Roman"
            run.font.size = Pt(10.5)
            if c_idx == 0:
                run.bold = True
    set_table_borders(dtbl, color="FFFFFF", val="none")

    add_para(doc, "", space_after=6)
    add_para(doc, "I / We hereby certify that this assessment complies with the University’s Rules and Regulations relating to Academic misconduct and plagiarism, as listed in the University Website, Regulations, and the Education Committee guidelines.", font_size=10.5, space_after=6)
    add_para(doc, "I / We confirm that all the work contained in this assessment is my / our own except where indicated, and that I / We have met the following conditions:", font_size=10.5, space_after=8)

    conditions = [
        "[X]  Clearly referenced / listed all sources as appropriate",
        "[X]  Referenced and put in inverted commas all quoted text (from books, web, etc)",
        "[X]  Given the sources of all pictures, data etc. that are not my own",
        "[X]  Not made any use of the report(s) or essay(s) of any other student(s) either past or present",
        "[X]  Acknowledged in appropriate places any help that I have received from others (e.g. fellow students, technicians, statisticians, external sources)",
        "[X]  Complied with any other plagiarism criteria specified in the Course handbook / University website"
    ]
    for c in conditions:
        add_para(doc, c, font_size=10, space_before=1, space_after=3)

    add_para(doc, "I / We understand that any false claim for this work will be penalized in accordance with the University policies and regulations.", font_size=10.5, space_before=6, space_after=12)

    # Declaration signature table
    dec_tbl = doc.add_table(rows=3, cols=1)
    dec_tbl.alignment = WD_TABLE_ALIGNMENT.CENTER
    cell_d0 = dec_tbl.cell(0, 0)
    cell_d0.width = Inches(6.2)
    set_cell_background(cell_d0, "F8FAFC")
    set_cell_margins(cell_d0, top=60, bottom=60, left=100, right=100)
    p = cell_d0.paragraphs[0]
    r = p.add_run("DECLARATION:")
    r.font.name = "Times New Roman"
    r.font.size = Pt(11)
    r.bold = True

    cell_d1 = dec_tbl.cell(1, 0)
    cell_d1.width = Inches(6.2)
    set_cell_margins(cell_d1, top=80, bottom=80, left=100, right=100)
    p1 = cell_d1.paragraphs[0]
    p1.paragraph_format.line_spacing = 1.2
    r1 = p1.add_run("I am aware of and understand the University’s policy on Academic misconduct and plagiarism and I certify that this assessment is my / our own work, except where indicated by referring, and that I have followed the good academic practices noted above.\n\n\n"
                    "<student 1’s sign>                                                                                             <student 2’s sign>\n"
                    "<student 1’s name>                                                                                             <student 2’s name>")
    r1.font.name = "Times New Roman"
    r1.font.size = Pt(10)

    cell_d2 = dec_tbl.cell(2, 0)
    cell_d2.width = Inches(6.2)
    set_cell_margins(cell_d2, top=60, bottom=60, left=100, right=100)
    p2 = cell_d2.paragraphs[0]
    r2 = p2.add_run("If you are working in a group, please write your registration numbers and sign with the date for every student in your group.")
    r2.font.name = "Times New Roman"
    r2.font.size = Pt(9.5)
    r2.italic = True
    set_table_borders(dec_tbl, color="94A3B8", sz="6", val="single")

    doc.add_page_break()

    # =========================================================================
    # PAGE 3: BONAFIDE CERTIFICATE (PRESERVED TEMPLATE STRUCTURE)
    # =========================================================================
    if os.path.exists("report_assets/extracted_ref/image2.jpeg"):
        p_logo3 = doc.add_paragraph()
        p_logo3.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_logo3.paragraph_format.space_before = Pt(0)
        p_logo3.paragraph_format.space_after = Pt(4)
        run_l3 = p_logo3.add_run()
        run_l3.add_picture("report_assets/extracted_ref/image2.jpeg", width=Inches(2.5))

    add_para(doc, "SRM INSTITUTE OF SCIENCE AND TECHNOLOGY KATTANKULATHUR – 603 203", font_size=13, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=4)
    add_para(doc, "<Font Style Times New Roman — Size - 18>", font_size=10, italic=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=6)
    add_para(doc, "BONAFIDE CERTIFICATE", font_size=16, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=4)
    add_para(doc, "<Font Style Times New Roman — Size - 16>", font_size=10, italic=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=2)
    add_para(doc, "<Font Style Times New Roman — Size - 14>", font_size=10, italic=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=16)

    add_para(doc, "Certified that 21CSP401L / 21CSP402L- Major Project  report titled “SUPPLYSYNC – ENTERPRISE SUPPLY CHAIN AND VENDOR MANAGEMENT SYSTEM” is the bonafide work of “STUDENT1 NAME [REG NUM], STUDENT2 NAME [REG NUM],” who carried out the project work under my supervision. Certified further, that to the best of my knowledge the work reported herein does not form any other project report or dissertation on the basis of which a degree or award was conferred on an earlier occasion on this or any other candidate.", font_size=11.5, align=WD_ALIGN_PARAGRAPH.JUSTIFY, space_before=10, space_after=50, line_spacing=1.4)

    # Signature blocks table
    sig_tbl = doc.add_table(rows=1, cols=3)
    sig_tbl.alignment = WD_TABLE_ALIGNMENT.CENTER
    sig_widths = [2.8, 0.6, 2.8]

    c_sup = sig_tbl.cell(0, 0)
    c_sup.width = Inches(sig_widths[0])
    p_sup = c_sup.paragraphs[0]
    p_sup.paragraph_format.line_spacing = 1.2
    p_sup.add_run("<<Signature >>\nSIGNATURE\n\n\n<<Name >>\nSUPERVISOR\n[Designation]\n[Department]")
    p_sup.runs[0].font.name = "Times New Roman"
    p_sup.runs[0].font.size = Pt(10.5)

    c_hod = sig_tbl.cell(0, 2)
    c_hod.width = Inches(sig_widths[2])
    p_hod = c_hod.paragraphs[0]
    p_hod.paragraph_format.line_spacing = 1.2
    p_hod.add_run("<<Signature >>\nSIGNATURE\n\n\n<< Name of the HoD>>\nPROFESSOR & HEAD\nDEPARTMENT OF\n<<name of the department in capitals >>")
    p_hod.runs[0].font.name = "Times New Roman"
    p_hod.runs[0].font.size = Pt(10.5)

    set_table_borders(sig_tbl, color="FFFFFF", val="none")

    doc.add_page_break()

    # =========================================================================
    # PAGE 4: ABSTRACT
    # =========================================================================
    add_para(doc, "ABSTRACT", font_size=16, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=10, space_after=2)
    add_para(doc, "( Abstract should be in Single Paragraph with 200 to 300 words )", font_size=10, italic=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=18)

    abstract_text = (
        "Modern enterprise supply networks face critical operational bottlenecks arising from fragmented communications, "
        "manual spreadsheet-driven procurement tracking, and inventory discrepancies that lead to costly stockouts or surplus carrying costs. "
        "To systematically mitigate these challenges, this project presents SupplySync – Enterprise Supply Chain and Vendor Management System, "
        "a centralized, web-based business management platform engineered using an industrial-grade three-tier architecture. "
        "The presentation tier is constructed using frameworkless HTML5, CSS3, Vanilla JavaScript, and Chart.js, delivering a lightweight, "
        "highly responsive single-page user interface that strictly eliminates heavy third-party framework overhead. "
        "The application tier is powered by Java 21 LTS and Spring Boot 3.3.4, incorporating Spring Web, Spring Security, Hibernate, and "
        "Spring Data JPA to enforce robust RESTful API contracts, role-based access control (RBAC), and stateless HMAC-SHA256 JWT authentication. "
        "Data persistence is managed via MySQL 8.0, featuring an eleven-table normalized relational schema that guarantees ACID transactional "
        "integrity across multi-entity operations. Key platform capabilities encompass comprehensive multi-tenant vendor onboarding, "
        "hierarchical product catalog categorization with SKU tracking, real-time inventory quantity synchronization with low-stock threshold triggers, "
        "and a structured procurement requisition approval lifecycle. Furthermore, the system implements end-to-end purchase order generation, "
        "multi-item automated tax calculation, carrier-integrated shipment milestone tracking, and an automated fulfillment engine that restocks "
        "inventory dynamically upon verified physical delivery. A comprehensive executive analytics suite provides real-time KPI metrics and "
        "instant CSV data export. Rigorous validation conducted via an automated sixteen-point end-to-end regression test suite demonstrates a "
        "100% test pass rate, establishing SupplySync as a resilient, scalable, and audit-compliant enterprise supply chain solution."
    )
    add_para(doc, abstract_text, font_size=11.5, align=WD_ALIGN_PARAGRAPH.JUSTIFY, space_before=6, space_after=14, line_spacing=1.35)

    doc.add_page_break()

    # =========================================================================
    # PAGE 5: TABLE OF CONTENTS, LIST OF FIGURES, LIST OF TABLES, ABBREVIATIONS
    # =========================================================================
    add_para(doc, "TABLE OF CONTENTS", font_size=16, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=10, space_after=16)

    toc_items = [
        ("ABSTRACT", "iv"),
        ("TABLE OF CONTENTS", "v"),
        ("LIST OF FIGURES", "vi"),
        ("LIST OF TABLES", "vii"),
        ("ABBREVIATIONS", "viii"),
        ("CHAPTER 1: INTRODUCTION", "1"),
        ("    1.1 General (Introduction to SupplySync SCM)", "1"),
        ("    1.2 Motivation", "2"),
        ("    1.3 Problem Statement and Description", "3"),
        ("    1.4 Sustainable Development Goal of the Project", "4"),
        ("    1.5 Product Vision Statement", "5"),
        ("    1.6 Product Goal", "6"),
        ("    1.7 Product Backlog (Key User Stories with Desired Outcomes)", "7"),
        ("    1.8 Product Release Plan / Roadmap", "8"),
        ("CHAPTER 2: SPRINT PLANNING AND EXECUTION", "9"),
        ("    2.1 Sprint 1: Core System Foundation, Security & Master Catalog Architecture", "10"),
        ("        2.1.1 Sprint Goal with User Stories of Sprint 1", "10"),
        ("        2.1.2 Functional Document", "12"),
        ("        2.1.3 Architecture Document", "14"),
        ("        2.1.4 UI Design", "16"),
        ("        2.1.5 Functional Test Cases", "17"),
        ("        2.1.6 Daily Call Progress", "18"),
        ("        2.1.7 Committed vs Completed User Stories", "19"),
        ("        2.1.8 Sprint Retrospective", "20"),
        ("    2.2 Sprint 2: Real-Time Inventory Control & Procurement Requisition Lifecycle", "21"),
        ("        2.2.1 Sprint Goal with User Stories of Sprint 2", "21"),
        ("        2.2.2 Functional Document", "22"),
        ("        2.2.3 Architecture Document", "24"),
        ("        2.2.4 UI Design", "25"),
        ("        2.2.5 Functional Test Cases", "26"),
        ("        2.2.6 Daily Call Progress", "27"),
        ("        2.2.7 Committed vs Completed User Stories", "28"),
        ("        2.2.8 Sprint Retrospective", "29"),
        ("    2.3 Sprint 3: Purchase Order Lifecycle, Logistics/Shipment Tracking & BI Analytics", "30"),
        ("        2.3.1 Sprint Goal with User Stories of Sprint 3", "30"),
        ("        2.3.2 Functional Document", "31"),
        ("        2.3.3 Architecture Document", "33"),
        ("        2.3.4 UI Design", "34"),
        ("        2.3.5 Functional Test Cases", "35"),
        ("        2.3.6 Daily Call Progress", "36"),
        ("        2.3.7 Committed vs Completed User Stories", "37"),
        ("        2.3.8 Sprint Retrospective", "38"),
        ("CHAPTER 3: RESULTS AND DISCUSSION", "39"),
        ("    3.1 Project Outcomes & Functional Alignment", "39"),
        ("    3.2 Committed vs Completed User Stories (Overall Release Velocity)", "41"),
        ("CHAPTER 4: CONCLUSIONS & FUTURE ENHANCEMENTS", "43"),
        ("    4.1 Conclusions", "43"),
        ("    4.2 Future Enhancements", "44"),
        ("APPENDIX", "45"),
        ("    A. PATENT DISCLOSURE FORM", "45"),
        ("    B. SAMPLE CODING", "48"),
        ("    C. PLAGIARISM REPORT", "52"),
        ("REFERENCES & BIBLIOGRAPHY", "53")
    ]

    toc_tbl = doc.add_table(rows=len(toc_items), cols=2)
    toc_tbl.alignment = WD_TABLE_ALIGNMENT.CENTER
    for idx, (title, page) in enumerate(toc_items):
        r = toc_tbl.rows[idx]
        is_ch = title.startswith("CHAPTER") or title in ["ABSTRACT", "TABLE OF CONTENTS", "LIST OF FIGURES", "LIST OF TABLES", "ABBREVIATIONS", "APPENDIX", "REFERENCES & BIBLIOGRAPHY"]
        cell_t = r.cells[0]
        cell_t.width = Inches(5.4)
        p_t = cell_t.paragraphs[0]
        p_t.paragraph_format.space_before = Pt(2)
        p_t.paragraph_format.space_after = Pt(2)
        run_t = p_t.add_run(title)
        run_t.font.name = "Times New Roman"
        run_t.font.size = Pt(10.5)
        run_t.bold = is_ch

        cell_p = r.cells[1]
        cell_p.width = Inches(0.8)
        p_p = cell_p.paragraphs[0]
        p_p.alignment = WD_ALIGN_PARAGRAPH.RIGHT
        p_p.paragraph_format.space_before = Pt(2)
        p_p.paragraph_format.space_after = Pt(2)
        run_p = p_p.add_run(page)
        run_p.font.name = "Times New Roman"
        run_p.font.size = Pt(10.5)
        run_p.bold = is_ch
    set_table_borders(toc_tbl, color="FFFFFF", val="none")

    doc.add_page_break()

    # LIST OF FIGURES
    add_para(doc, "LIST OF FIGURES", font_size=16, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=10, space_after=16)
    lof_items = [
        ("Figure 1.1", "MS Planner Agile Board of SupplySync Enterprise SCM", "7"),
        ("Figure 1.2", "SupplySync SCM 12-Week Product Release Roadmap", "8"),
        ("Figure 2.1", "Detailed User Story Card – Role-Based Authentication & Session Security", "11"),
        ("Figure 2.2", "Detailed User Story Card – Multi-Vendor Profile & Master Catalog Management", "11"),
        ("Figure 2.3", "Detailed User Story Card – Global Product Catalog & Category Taxonomy", "12"),
        ("Figure 2.4", "SupplySync 3-Tier Enterprise Web Application Architecture Diagram", "15"),
        ("Figure 2.5", "SupplySync Database Entity-Relationship (ER) Architecture Diagram", "15"),
        ("Figure 2.6", "Daily Scrum Standup Meeting Log – Sprint 1 Checkpoint", "18"),
        ("Figure 2.7", "Sprint 1 Committed vs Completed User Stories Bar Chart", "19"),
        ("Figure 2.8", "Procurement Requisition & Stock Movement Workflow State Diagram", "24"),
        ("Figure 2.9", "UI Design – Live Application Procurement Requisition Modal", "25"),
        ("Figure 2.10", "Daily Scrum Standup Meeting Log – Sprint 2 Checkpoint", "27"),
        ("Figure 2.11", "Sprint 2 Committed vs Completed User Stories Bar Chart", "28"),
        ("Figure 2.12", "UI Design – Live Application Purchase Order Generation Screen", "34"),
        ("Figure 2.13", "UI Design – Live Application Enterprise Analytics & Executive Dashboard", "35"),
        ("Figure 2.14", "Daily Scrum Standup Meeting Log – Sprint 3 Checkpoint", "36"),
        ("Figure 2.15", "Sprint 3 Committed vs Completed User Stories Bar Chart", "37"),
        ("Figure 3.1", "SupplySync Enterprise SCM – Overall Velocity Across All Sprints", "41")
    ]
    lof_tbl = doc.add_table(rows=len(lof_items) + 1, cols=3)
    lof_tbl.alignment = WD_TABLE_ALIGNMENT.CENTER
    format_table_header(lof_tbl.rows[0], [1.3, 4.2, 0.7], ["FIGURE NO.", "TITLE", "PAGE NO."])
    for idx, (fnum, ftitle, fpage) in enumerate(lof_items):
        format_table_row(lof_tbl.rows[idx + 1], [1.3, 4.2, 0.7], [fnum, ftitle, fpage], is_even=(idx%2==1), aligns=[WD_ALIGN_PARAGRAPH.LEFT, WD_ALIGN_PARAGRAPH.LEFT, WD_ALIGN_PARAGRAPH.RIGHT])
    set_table_borders(lof_tbl)

    doc.add_page_break()

    # LIST OF TABLES
    add_para(doc, "LIST OF TABLES", font_size=16, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=10, space_after=16)
    lot_items = [
        ("Table 1.1", "Master Product Backlog with MoSCoW Prioritization", "7"),
        ("Table 2.1", "Detailed User Stories of Sprint 1 (Core Foundation & Catalogs)", "10"),
        ("Table 2.2", "SupplySync Role-Based Access Control (RBAC) Authorization Matrix", "13"),
        ("Table 2.3", "Functional Test Execution Suite for Sprint 1 Modules", "17"),
        ("Table 2.4", "Sprint 1 Retrospective (Liked, Learned, Lacked, Longed For)", "20"),
        ("Table 2.5", "Detailed User Stories of Sprint 2 (Inventory & Procurement)", "21"),
        ("Table 2.6", "Functional Test Execution Suite for Sprint 2 Modules", "26"),
        ("Table 2.7", "Sprint 2 Retrospective (Liked, Learned, Lacked, Longed For)", "29"),
        ("Table 2.8", "Detailed User Stories of Sprint 3 (Purchase Orders & Logistics)", "30"),
        ("Table 2.9", "Functional Test Execution Suite for Sprint 3 Modules", "35"),
        ("Table 2.10", "Sprint 3 Retrospective (Liked, Learned, Lacked, Longed For)", "38"),
        ("Table 3.1", "Executive Summary of Production Outcomes and Goal Alignment", "40")
    ]
    lot_tbl = doc.add_table(rows=len(lot_items) + 1, cols=3)
    lot_tbl.alignment = WD_TABLE_ALIGNMENT.CENTER
    format_table_header(lot_tbl.rows[0], [1.3, 4.2, 0.7], ["TABLE NO.", "TITLE", "PAGE NO."])
    for idx, (tnum, ttitle, tpage) in enumerate(lot_items):
        format_table_row(lot_tbl.rows[idx + 1], [1.3, 4.2, 0.7], [tnum, ttitle, tpage], is_even=(idx%2==1), aligns=[WD_ALIGN_PARAGRAPH.LEFT, WD_ALIGN_PARAGRAPH.LEFT, WD_ALIGN_PARAGRAPH.RIGHT])
    set_table_borders(lot_tbl)

    doc.add_page_break()

    # ABBREVIATIONS
    add_para(doc, "ABBREVIATIONS", font_size=16, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=10, space_after=16)
    abbrevs = [
        ("ACID", "Atomicity, Consistency, Isolation, Durability"),
        ("API", "Application Programming Interface"),
        ("BCrypt", "Blowfish-based Password Hashing Function"),
        ("BI", "Business Intelligence"),
        ("CRUD", "Create, Read, Update, Delete"),
        ("CSV", "Comma-Separated Values"),
        ("DAO", "Data Access Object"),
        ("DTO", "Data Transfer Object"),
        ("ERP", "Enterprise Resource Planning"),
        ("FK", "Foreign Key"),
        ("HTTP", "Hypertext Transfer Protocol"),
        ("IDE", "Integrated Development Environment"),
        ("JDBC", "Java Database Connectivity"),
        ("JPA", "Java Persistence API"),
        ("JSON", "JavaScript Object Notation"),
        ("JWT", "JSON Web Token"),
        ("KPI", "Key Performance Indicator"),
        ("ORM", "Object-Relational Mapping"),
        ("PK", "Primary Key"),
        ("PO", "Purchase Order"),
        ("PR", "Procurement Requisition"),
        ("RBAC", "Role-Based Access Control"),
        ("REST", "Representational State Transfer"),
        ("SCM", "Supply Chain Management"),
        ("SKU", "Stock Keeping Unit"),
        ("SPA", "Single Page Application"),
        ("SQL", "Structured Query Language"),
        ("UI", "User Interface"),
        ("UOM", "Unit of Measurement"),
        ("UX", "User Experience")
    ]
    abbr_tbl = doc.add_table(rows=len(abbrevs) + 1, cols=2)
    abbr_tbl.alignment = WD_TABLE_ALIGNMENT.CENTER
    format_table_header(abbr_tbl.rows[0], [1.8, 4.4], ["ACRONYM", "EXPANSION / DEFINITION"])
    for idx, (acr, exp) in enumerate(abbrevs):
        format_table_row(abbr_tbl.rows[idx + 1], [1.8, 4.4], [acr, exp], is_even=(idx%2==1))
    set_table_borders(abbr_tbl)

    doc.add_page_break()

    # =========================================================================
    # CHAPTER 1: INTRODUCTION
    # =========================================================================
    add_para(doc, "CHAPTER 1", font_size=16, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=10, space_after=2)
    add_para(doc, "INTRODUCTION", font_size=16, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=16)

    add_heading_2(doc, "1.1 General (Introduction to SupplySync SCM)")
    add_para(doc, 
        "In the contemporary industrial ecosystem, supply chain resilience, operational transparency, and rapid procurement execution "
        "serve as decisive determinants of enterprise competitiveness. Modern enterprises operate within intricate webs of vendors, suppliers, "
        "distributors, and internal departments. Effective coordination among these diverse stakeholders requires real-time data synchronization "
        "across inventory warehouses, procurement departments, and external supplier networks. SupplySync – Enterprise Supply Chain and "
        "Vendor Management System is conceptualized and engineered as a high-performance, centralized SaaS web platform designed to streamline, "
        "automate, and monitor the entire operational lifecycle of enterprise procurement and physical logistics.",
        align=WD_ALIGN_PARAGRAPH.JUSTIFY)
    add_para(doc,
        "Engineered on a decoupled three-tier architectural paradigm, SupplySync integrates a lightweight, highly responsive client presentation tier "
        "built entirely with Vanilla JavaScript, HTML5, and CSS3, eliminating the compilation latency and heavy dependencies of external frontend "
        "frameworks. The backend architecture leverages Java 21 LTS and the Spring Boot 3.3.4 enterprise framework, orchestrating transactional business "
        "logic through Spring Web REST controllers, Spring Security 6, Hibernate Object-Relational Mapping (ORM), and Spring Data JPA. Relational "
        "data persistence is enforced through MySQL 8.0 with rigorous foreign key constraints, composite indexes, and ACID guarantees. "
        "By integrating dual role-based access for Enterprise Administrators and Third-Party Vendors, SupplySync bridges communication chasms, "
        "providing a singular source of truth for inventory balances, purchase orders, delivery statuses, and business intelligence.",
        align=WD_ALIGN_PARAGRAPH.JUSTIFY)

    add_heading_2(doc, "1.2 Motivation")
    add_para(doc,
        "The motivation driving the development of SupplySync emerges from pervasive inefficiencies observed in traditional supply chain management. "
        "A substantial proportion of small-to-medium enterprises (SMEs) and educational institutions continue to manage multi-million-dollar inventory "
        "operations using legacy spreadsheets, disconnected desktop software, or unstructured email correspondence. Such archaic practices "
        "inevitably suffer from severe human transcription errors, delayed procurement reaction times, stockout losses during demand surges, and "
        "excessive capital lockup due to overstocking.",
        align=WD_ALIGN_PARAGRAPH.JUSTIFY)
    add_para(doc,
        "Furthermore, suppliers and vendors are frequently isolated from procurement systems, lacking visibility into purchase order fulfillment status, "
        "shipment milestone acknowledgments, and pending requisitions. This lack of vendor integration results in endless back-and-forth communication, "
        "delayed deliveries, and disputed invoices. Developing an integrated platform where both enterprise administrators and vendors interact on a "
        "unified, secure, and auditable medium is therefore an imperative operational necessity. SupplySync directly addresses these vulnerabilities by "
        "offering real-time inventory tracking, automated threshold-based reorder alerts, end-to-end purchase order workflows, and transparent "
        "logistics tracking.",
        align=WD_ALIGN_PARAGRAPH.JUSTIFY)

    add_heading_2(doc, "1.3 Problem Statement and Description")
    add_para(doc,
        "Contemporary enterprise supply chains are burdened by fragmented communication channels, asynchronous manual record-keeping, and "
        "unreliable inventory visibility. When an inventory item drops below its critical safety buffer, warehouse staff must manually notify "
        "purchasing managers, who in turn draft manual purchase orders and email them to external vendors. Vendors acknowledge orders asynchronously, "
        "often without structured delivery timelines, leading to untracked shipments and stockouts that disrupt production lines. "
        "Moreover, upon the physical arrival of shipments at warehouse loading docks, receiving clerks must manually adjust stock balances, "
        "introducing discrepancies between physical warehouse counts and digital ledgers.",
        align=WD_ALIGN_PARAGRAPH.JUSTIFY)
    add_para(doc,
        "The primary problem addressed by SupplySync is the elimination of asynchronous, error-prone manual intervention through an automated, "
        "event-driven digital supply platform. The proposed platform must provide: (1) unified multi-tenant authentication distinguishing administrators "
        "from external suppliers, (2) strict data validation preventing negative stock drawdowns, (3) automated inventory intake triggered by verified "
        "delivery status updates, and (4) real-time interactive dashboards that translate raw operational logs into actionable business intelligence.",
        align=WD_ALIGN_PARAGRAPH.JUSTIFY)

    add_heading_2(doc, "1.4 Sustainable Development Goal of the Project")
    add_para(doc,
        "SupplySync directly advances the United Nations Sustainable Development Goals (SDGs), with targeted alignment to:",
        align=WD_ALIGN_PARAGRAPH.JUSTIFY)
    add_para(doc,
        "• SDG 9: Industry, Innovation, and Infrastructure (Target 9.4): By upgrading supply chain technological capabilities and modernizing legacy "
        "procurement operations into automated, digital cloud-ready pipelines, SupplySync increases resource-use efficiency and fosters sustainable industrialization.",
        align=WD_ALIGN_PARAGRAPH.JUSTIFY)
    add_para(doc,
        "• SDG 12: Responsible Consumption and Production (Target 12.2): By maintaining accurate, real-time inventory tracking and dynamic low-stock alerts, "
        "SupplySync prevents over-procurement, reduces obsolescence of stored industrial goods, minimizes warehousing waste, and ensures optimal resource utilization.",
        align=WD_ALIGN_PARAGRAPH.JUSTIFY)

    add_heading_2(doc, "1.5 Product Vision Statement")
    add_heading_3(doc, "1.5.1 Audience:")
    add_para(doc, "• Primary Audience: Enterprise Supply Chain Managers, Inventory Controllers, Warehouse Staff, and Procurement Executives who oversee daily purchasing, stock levels, and vendor compliance.")
    add_para(doc, "• Secondary Audience: Registered Third-Party Suppliers, Logistics Coordinators, and Vendor Account Representatives who fulfill purchase orders and track shipments.")

    add_heading_3(doc, "1.5.2 Needs:")
    add_para(doc, "• Primary Needs: Centralized real-time inventory tracking; automated low-stock warnings; streamlined purchase order generation; transparent supplier onboarding; role-based security preventing unauthorized modifications.")
    add_para(doc, "• Secondary Needs: Historical audit logs for stock movements; dynamic graphical analytics for procurement spending; rapid CSV export for accounting and regulatory audits.")

    add_heading_3(doc, "1.5.3 Products:")
    add_para(doc, "• Core Product: A responsive, full-stack enterprise web application delivering comprehensive SCM functionality via 14 intuitive user interfaces backed by robust RESTful APIs.")
    add_para(doc, "• Key Features: Automated inventory intake upon physical delivery; pessimistic stock locking; multi-item tax calculation engine; carrier shipment tracking; live KPI dashboard.")

    add_heading_3(doc, "1.5.4 Values:")
    add_para(doc, "• Core Values: Operational transparency, data integrity, transactional consistency, zero-trust security, and frameworkless frontend speed.")
    add_para(doc, "• Differentiators: Pure Vanilla JS architecture requiring no Node.js runtime build steps; automated delivery-to-inventory reactive updates; single-click role switching for evaluation demos.")

    add_heading_2(doc, "1.6 Product Goal")
    add_para(doc,
        "The overarching goal of SupplySync is to eliminate supply chain opacity, minimize procurement fulfillment cycles by over 60%, and "
        "guarantee 100% stock ledger accuracy across enterprise warehouses and distributed vendor networks. By centralizing procurement workflows, "
        "automating stock ledger reconciliations, and providing dedicated portals for vendors and administrators, SupplySync empowers enterprises "
        "to scale operations while maintaining rigorous audit compliance.",
        align=WD_ALIGN_PARAGRAPH.JUSTIFY)

    add_heading_2(doc, "1.7 Product Backlog")
    add_para(doc,
        "The product backlog was structured adhering to Agile Scrum principles, utilizing MoSCoW (Must have, Should have, Could have, Won't have) "
        "prioritization. Table 1.1 delineates the core user stories comprising the SupplySync release backlog.",
        align=WD_ALIGN_PARAGRAPH.JUSTIFY)

    # Table 1.1 Product Backlog
    backlog_data = [
        ("US#1", "As an Admin/Vendor, I want to securely log in via credentials to access authorized features.", "Must Have", "Functional", "Sprint 1"),
        ("US#2", "As an Admin, I want to onboard, edit, and search vendors to maintain a centralized supplier catalog.", "Must Have", "Functional", "Sprint 1"),
        ("US#3", "As an Admin, I want to manage categories and products with SKUs, pricing, and UOM.", "Must Have", "Functional", "Sprint 1"),
        ("US#4", "As an Inventory Controller, I want real-time stock quantity tracking with low-stock alerts.", "Must Have", "Functional", "Sprint 2"),
        ("US#5", "As a Warehouse Manager, I want negative inventory guards preventing invalid stock drawdowns.", "Must Have", "Business Rule", "Sprint 2"),
        ("US#6", "As a User, I want to create and approve procurement requisitions with calculated estimates.", "Must Have", "Functional", "Sprint 2"),
        ("US#7", "As an Admin, I want to generate purchase orders with multi-line items, taxes, and vendor terms.", "Must Have", "Functional", "Sprint 3"),
        ("US#8", "As a Logistics Clerk, I want to track shipments with carrier names, tracking numbers, and delivery dates.", "Must Have", "Functional", "Sprint 3"),
        ("US#9", "As an Inventory Controller, I want deliveries marked 'DELIVERED' to automatically restock warehouse inventory.", "Must Have", "Automation", "Sprint 3"),
        ("US#10", "As an Auditor, I want an immutable chronological audit trail of all manual and automated stock movements.", "Should Have", "Compliance", "Sprint 2"),
        ("US#11", "As an Executive, I want an interactive KPI dashboard with monthly spending and inventory charts.", "Must Have", "BI Analytics", "Sprint 3"),
        ("US#12", "As an Accountant, I want one-click CSV report exports for inventory, vendors, and orders.", "Should Have", "Reporting", "Sprint 3")
    ]
    tbl_pb = doc.add_table(rows=len(backlog_data) + 1, cols=5)
    tbl_pb.alignment = WD_TABLE_ALIGNMENT.CENTER
    format_table_header(tbl_pb.rows[0], [0.8, 3.2, 1.0, 1.0, 1.0], ["ID", "USER STORY SPECIFICATION", "PRIORITY", "TYPE", "SPRINT"])
    for idx, row in enumerate(backlog_data):
        format_table_row(tbl_pb.rows[idx + 1], [0.8, 3.2, 1.0, 1.0, 1.0], list(row), is_even=(idx%2==1))
    set_table_borders(tbl_pb)

    add_figure(doc, "report_assets/figures/fig1_1_ms_planner_board.png", "Figure 1.1: MS Planner Agile Board of SupplySync Enterprise SCM", width_in=6.2)

    add_heading_2(doc, "1.8 Product Release Plan")
    add_para(doc,
        "The project release roadmap was organized into a 12-week schedule partitioned across three major development sprints. "
        "Figure 1.2 illustrates the project release Gantt roadmap detailing feature progression from architecture design to final production hardening.",
        align=WD_ALIGN_PARAGRAPH.JUSTIFY)

    add_figure(doc, "report_assets/figures/fig1_2_release_plan.png", "Figure 1.2: SupplySync SCM 12-Week Product Release Roadmap", width_in=6.2)

    doc.add_page_break()

    # =========================================================================
    # CHAPTER 2: SPRINT PLANNING AND EXECUTION
    # =========================================================================
    add_para(doc, "CHAPTER 2", font_size=16, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=10, space_after=2)
    add_para(doc, "SPRINT PLANNING AND EXECUTION", font_size=16, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=16)

    # -------------------------------------------------------------------------
    # 2.1 SPRINT 1
    # -------------------------------------------------------------------------
    add_heading_1(doc, "2.1 Sprint 1: Core System Foundation, Security & Master Catalog Architecture")
    add_heading_2(doc, "2.1.1 Sprint Goal with User Stories of Sprint 1")
    add_para(doc,
        "The primary goal of Sprint 1 was to establish the architectural foundation of the SupplySync platform. This included setting up the "
        "Java 21 Spring Boot 3 backend project, configuring Spring Security with stateless HMAC-SHA256 JWT authentication, establishing the "
        "MySQL 8 database schema for master data entities, and building the master catalog modules for Vendor, Category, and Product management.",
        align=WD_ALIGN_PARAGRAPH.JUSTIFY)

    s1_stories = [
        ("US #1", "As a registered enterprise user, I want to securely authenticate via my email and password using BCrypt password hashing and receive a stateless JWT token so that my session is authenticated and protected across all requests."),
        ("US #2", "As an enterprise administrator, I want to create, read, update, and deactivate vendor profiles with company details, business category, and contact information so that supplier relationships are centrally maintained."),
        ("US #3", "As an inventory controller, I want to create and categorize products with unique SKUs, descriptions, units of measurement (UOM), and pricing so that inventory operations reference verified catalog specifications.")
    ]
    tbl_s1 = doc.add_table(rows=len(s1_stories) + 1, cols=2)
    tbl_s1.alignment = WD_TABLE_ALIGNMENT.CENTER
    format_table_header(tbl_s1.rows[0], [1.0, 5.2], ["S.NO", "DETAILED USER STORIES OF SPRINT 1"])
    for idx, row in enumerate(s1_stories):
        format_table_row(tbl_s1.rows[idx + 1], [1.0, 5.2], list(row), is_even=(idx%2==1))
    set_table_borders(tbl_s1)

    add_figure(doc, "report_assets/figures/fig2_1_us1_auth.png", "Figure 2.1: Detailed User Story Card – Role-Based Authentication & Session Security", width_in=5.8)
    add_figure(doc, "report_assets/figures/fig2_2_us2_vendor.png", "Figure 2.2: Detailed User Story Card – Multi-Vendor Profile & Master Catalog Management", width_in=5.8)
    add_figure(doc, "report_assets/figures/fig2_3_us3_products.png", "Figure 2.3: Detailed User Story Card – Global Product Catalog & Category Taxonomy", width_in=5.8)

    add_heading_2(doc, "2.1.2 Functional Document")
    add_heading_3(doc, "2.1.2.1 Introduction:")
    add_para(doc, "Sprint 1 focuses on core authentication, access control boundaries, and master catalog maintenance which serve as prerequisites for all downstream supply chain transactions.")
    add_heading_3(doc, "2.1.2.2 Product Goal:")
    add_para(doc, "Ensure zero-trust security compliance through cryptographic password hashing, stateless session validation, and relational integrity across foundational database entities.")
    add_heading_3(doc, "2.1.2.3 Demography (Users, Location):")
    add_para(doc, "Users: Enterprise Administrators, Inventory Managers, Purchasing Officers, and Registered Third-Party Vendors. Location: Enterprise headquarters, regional warehouses, and remote supplier facilities operating over HTTPS.")
    add_heading_3(doc, "2.1.2.4 Business Processes:")
    add_para(doc, "User Authentication & Authorization: Incoming login requests validate against BCrypt password hashes in MySQL. Upon successful validation, the backend generates an HMAC-SHA256 signed JWT containing user claims and role authorities. Master Catalog Management: Administrators onboard approved suppliers and assign catalog products to designated business categories.")

    add_heading_3(doc, "2.1.2.5 Features:")
    add_para(doc, "• Feature 1: JWT Authentication & Role-Based Authorization – Stateless token-based security filter intercepting every protected REST endpoint.\n"
                  "• Feature 2: Vendor Profile Management – Full CRUD capabilities with search, filtering, and active/inactive status toggling.\n"
                  "• Feature 3: Product & Category Catalog – Hierarchical catalog management with unique SKU constraints and pricing definitions.")

    add_heading_3(doc, "2.1.2.6 Authorization Matrix:")
    auth_matrix = [
        ("Administrator", "Full unrestricted access to all 14 modules, vendor management, product catalog, user administration, and system settings."),
        ("Vendor Representative", "Restricted access to Vendor Portal: view assigned purchase orders, update shipment tracking, submit requisitions, and manage company profile."),
        ("Unauthenticated / Public", "Access restricted solely to login page, static CSS/JS assets, and authentication verification endpoints.")
    ]
    tbl_auth = doc.add_table(rows=len(auth_matrix) + 1, cols=2)
    tbl_auth.alignment = WD_TABLE_ALIGNMENT.CENTER
    format_table_header(tbl_auth.rows[0], [1.8, 4.4], ["ROLE", "ACCESS LEVEL & PERMISSION SCOPE"])
    for idx, row in enumerate(auth_matrix):
        format_table_row(tbl_auth.rows[idx + 1], [1.8, 4.4], list(row), is_even=(idx%2==1))
    set_table_borders(tbl_auth)

    add_heading_3(doc, "2.1.2.7 Assumptions:")
    add_para(doc, "• All users have access to modern evergreen web browsers (Chrome, Edge, Firefox, Safari) with JavaScript enabled.\n"
                  "• The MySQL 8 database service maintains continuous availability on port 3306 with UTF-8 character encoding.\n"
                  "• Network connectivity between client browsers and the Spring Boot server remains stable during active sessions.")

    add_heading_2(doc, "2.1.3 Architecture Document")
    add_heading_3(doc, "2.1.3.1 Application Modules:")
    add_para(doc, "The backend architecture is structured around modular Spring components: AuthService, UserService, VendorService, CategoryService, ProductService, and CustomUserDetailsService, cleanly decoupled from HTTP controllers.")

    add_figure(doc, "report_assets/figures/fig2_4_system_architecture.png", "Figure 2.4: SupplySync 3-Tier Enterprise Web Application Architecture Diagram", width_in=6.2)
    add_figure(doc, "report_assets/figures/fig2_5_er_diagram.png", "Figure 2.5: SupplySync Database Entity-Relationship (ER) Architecture Diagram", width_in=6.2)

    add_heading_3(doc, "2.1.3.3 Data Exchange Contract:")
    add_para(doc, "• Frequency of Data Exchanges: Real-time synchronous HTTP request-response cycle for authentication and catalog CRUD operations.\n"
                  "• Datasets: User credentials (JSON), JWT tokens (Bearer header), Vendor DTOs, Product DTOs, Category DTOs.\n"
                  "• Mode of Exchanges: JSON payloads serialized over RESTful HTTP/1.1 protocols using standard status codes (200 OK, 201 Created, 400 Bad Request, 401 Unauthorized, 403 Forbidden, 404 Not Found).")

    add_heading_2(doc, "2.1.4 UI Design")
    add_para(doc,
        "The user interface follows a modern enterprise design system characterized by clean white cards, a navy blue sidebar (#0f172a), "
        "professional blue accents (#2563eb), and subtle elevation shadows. Responsive CSS media queries ensure optimal usability across "
        "desktops, tablets, and mobile devices.",
        align=WD_ALIGN_PARAGRAPH.JUSTIFY)

    add_heading_2(doc, "2.1.5 Functional Test Cases")
    tc_s1 = [
        ("Authentication", "Valid Admin Login", "Submit admin@supplysync.com / admin123", "200 OK, returns valid JWT token and user profile", "Token generated successfully, redirected to dashboard", "Pass"),
        ("Authentication", "Invalid Password", "Submit admin@supplysync.com / wrongpass", "401 Unauthorized with descriptive error message", "401 received, 'Invalid credentials' displayed", "Pass"),
        ("Vendor Catalog", "Create New Vendor", "POST /api/vendors with company payload", "201 Created, vendor record saved in MySQL", "Vendor created with ID, visible in vendor table", "Pass"),
        ("Product Catalog", "Create Product with SKU", "POST /api/products with SKU 'ELEC-THM-50G'", "201 Created, inventory initialized automatically", "Product persisted, inventory record linked with 0 stock", "Pass")
    ]
    tbl_tc1 = doc.add_table(rows=len(tc_s1) + 1, cols=6)
    tbl_tc1.alignment = WD_TABLE_ALIGNMENT.CENTER
    format_table_header(tbl_tc1.rows[0], [1.0, 1.1, 1.3, 1.2, 1.2, 0.6], ["FEATURE", "TEST CASE", "STEPS", "EXPECTED", "ACTUAL", "STATUS"])
    for idx, row in enumerate(tc_s1):
        format_table_row(tbl_tc1.rows[idx + 1], [1.0, 1.1, 1.3, 1.2, 1.2, 0.6], list(row), is_even=(idx%2==1), aligns=[WD_ALIGN_PARAGRAPH.LEFT, WD_ALIGN_PARAGRAPH.LEFT, WD_ALIGN_PARAGRAPH.LEFT, WD_ALIGN_PARAGRAPH.LEFT, WD_ALIGN_PARAGRAPH.LEFT, WD_ALIGN_PARAGRAPH.CENTER])
    set_table_borders(tbl_tc1)

    add_figure(doc, "report_assets/figures/fig2_6_standup_sprint1.png", "Figure 2.6: Daily Scrum Standup Meeting Log – Sprint 1 Checkpoint", width_in=5.8)
    add_figure(doc, "report_assets/figures/fig2_7_sprint1_bar.png", "Figure 2.7: Sprint 1 Committed vs Completed User Stories Bar Chart", width_in=4.2)

    add_heading_2(doc, "2.1.8 Sprint Retrospective")
    retro_s1 = [
        ("Liked", "Seamless integration between Spring Security and JJWT library; clean responsive layout achieved with pure CSS3 variables and flexbox."),
        ("Learned", "Pre-flight CORS options handling in Spring Security must explicitly permit all necessary headers for smooth SPA client execution."),
        ("Lacked", "Initial lack of comprehensive seed scripts for multi-vendor demonstration required manual database insertions before DataInitializer was implemented."),
        ("Longed For", "Automated UI test harness to verify token injection across all 14 static HTML pages simultaneously.")
    ]
    tbl_ret1 = doc.add_table(rows=len(retro_s1) + 1, cols=2)
    tbl_ret1.alignment = WD_TABLE_ALIGNMENT.CENTER
    format_table_header(tbl_ret1.rows[0], [1.5, 4.7], ["CATEGORY", "SPRINT 1 RETROSPECTIVE OBSERVATIONS"])
    for idx, row in enumerate(retro_s1):
        format_table_row(tbl_ret1.rows[idx + 1], [1.5, 4.7], list(row), is_even=(idx%2==1))
    set_table_borders(tbl_ret1)

    doc.add_page_break()

    # -------------------------------------------------------------------------
    # 2.2 SPRINT 2
    # -------------------------------------------------------------------------
    add_heading_1(doc, "2.2 Sprint 2: Real-Time Inventory Control & Procurement Requisition Lifecycle")
    add_heading_2(doc, "2.2.1 Sprint Goal with User Stories of Sprint 2")
    add_para(doc,
        "The objective of Sprint 2 was to implement real-time inventory management, transactional stock movement ledgers, "
        "safety stock threshold triggers, and the complete procurement requisition state machine.",
        align=WD_ALIGN_PARAGRAPH.JUSTIFY)

    s2_stories = [
        ("US #4", "As an inventory controller, I want to view real-time available stock quantities, minimum buffer thresholds, and maximum levels with visual low-stock alerts so that inventory replenishment is proactively triggered."),
        ("US #5", "As a warehouse supervisor, I want to execute manual stock adjustments (STOCK_ADDED, STOCK_REMOVED, MANUAL_ADJUSTMENT) with validation guards preventing negative inventory drawdowns."),
        ("US #6", "As a procurement officer, I want to draft, submit, and approve procurement requisitions with target vendors, estimated costs, and line-item breakdowns.")
    ]
    tbl_s2 = doc.add_table(rows=len(s2_stories) + 1, cols=2)
    tbl_s2.alignment = WD_TABLE_ALIGNMENT.CENTER
    format_table_header(tbl_s2.rows[0], [1.0, 5.2], ["S.NO", "DETAILED USER STORIES OF SPRINT 2"])
    for idx, row in enumerate(s2_stories):
        format_table_row(tbl_s2.rows[idx + 1], [1.0, 5.2], list(row), is_even=(idx%2==1))
    set_table_borders(tbl_s2)

    add_heading_2(doc, "2.2.2 Functional Document")
    add_para(doc,
        "Inventory business logic enforces transactional consistency via Spring's @Transactional annotation. Every inventory adjustment "
        "automatically writes an immutable audit record to the stock_transactions table detailing the product, delta quantity, transaction type, "
        "and timestamp. When available quantity falls below min_stock_level, the system marks the item as LOW_STOCK and displays high-visibility "
        "badges on the dashboard.",
        align=WD_ALIGN_PARAGRAPH.JUSTIFY)
    add_para(doc,
        "Procurement requisitions transition through a rigorous state machine: DRAFT -> PENDING -> APPROVED -> REJECTED -> CONVERTED_TO_ORDER. "
        "Only authorized administrators can transition requisitions to APPROVED, calculating total estimated expenditures dynamically.",
        align=WD_ALIGN_PARAGRAPH.JUSTIFY)

    add_heading_2(doc, "2.2.3 Architecture Document")
    add_para(doc,
        "InventoryService interacts directly with InventoryRepository and StockTransactionRepository. Pessimistic locking prevents race conditions "
        "during simultaneous warehouse transactions. ProcurementService orchestrates parent ProcurementRequest entities and child ProcurementRequestItem "
        "records through JPA cascading.",
        align=WD_ALIGN_PARAGRAPH.JUSTIFY)

    add_figure(doc, "report_assets/figures/fig2_9_procurement_real.png", "Figure 2.9: UI Design – Live Application Procurement Requisition Modal", width_in=6.0)

    add_heading_2(doc, "2.2.5 Functional Test Cases")
    tc_s2 = [
        ("Inventory", "Valid Stock Addition", "Add 50 units to product stock", "Stock increases by 50, audit log created", "Stock updated, STOCK_ADDED record logged", "Pass"),
        ("Inventory", "Negative Stock Guard", "Attempt to deduct 200 units from stock of 50", "400 Bad Request, 'Insufficient stock quantity'", "Transaction aborted, stock remains 50", "Pass"),
        ("Procurement", "Create Requisition", "Submit requisition with 2 line items", "201 Created, status PENDING, estimated total computed", "Requisition created with PR-REQ number", "Pass"),
        ("Procurement", "Approve Requisition", "Admin marks requisition APPROVED", "Status updated to APPROVED, ready for PO conversion", "Status changed, approval timestamp recorded", "Pass")
    ]
    tbl_tc2 = doc.add_table(rows=len(tc_s2) + 1, cols=6)
    tbl_tc2.alignment = WD_TABLE_ALIGNMENT.CENTER
    format_table_header(tbl_tc2.rows[0], [1.0, 1.1, 1.3, 1.2, 1.2, 0.6], ["FEATURE", "TEST CASE", "STEPS", "EXPECTED", "ACTUAL", "STATUS"])
    for idx, row in enumerate(tc_s2):
        format_table_row(tbl_tc2.rows[idx + 1], [1.0, 1.1, 1.3, 1.2, 1.2, 0.6], list(row), is_even=(idx%2==1), aligns=[WD_ALIGN_PARAGRAPH.LEFT, WD_ALIGN_PARAGRAPH.LEFT, WD_ALIGN_PARAGRAPH.LEFT, WD_ALIGN_PARAGRAPH.LEFT, WD_ALIGN_PARAGRAPH.LEFT, WD_ALIGN_PARAGRAPH.CENTER])
    set_table_borders(tbl_tc2)

    add_figure(doc, "report_assets/figures/fig2_10_standup_sprint2.png", "Figure 2.10: Daily Scrum Standup Meeting Log – Sprint 2 Checkpoint", width_in=5.8)
    add_figure(doc, "report_assets/figures/fig2_11_sprint2_bar.png", "Figure 2.11: Sprint 2 Committed vs Completed User Stories Bar Chart", width_in=4.2)

    add_heading_2(doc, "2.2.8 Sprint Retrospective")
    retro_s2 = [
        ("Liked", "Robust mathematical calculations for line-item subtotals and estimated expenditures across multiple items."),
        ("Learned", "Cascading delete constraints in Hibernate must be explicitly configured to prevent orphaned requisition line items."),
        ("Lacked", "Visual interactive stock movement graphs during initial sprint kickoff."),
        ("Longed For", "Automated low-stock notification triggers sent directly to vendor representatives.")
    ]
    tbl_ret2 = doc.add_table(rows=len(retro_s2) + 1, cols=2)
    tbl_ret2.alignment = WD_TABLE_ALIGNMENT.CENTER
    format_table_header(tbl_ret2.rows[0], [1.5, 4.7], ["CATEGORY", "SPRINT 2 RETROSPECTIVE OBSERVATIONS"])
    for idx, row in enumerate(retro_s2):
        format_table_row(tbl_ret2.rows[idx + 1], [1.5, 4.7], list(row), is_even=(idx%2==1))
    set_table_borders(tbl_ret2)

    doc.add_page_break()

    # -------------------------------------------------------------------------
    # 2.3 SPRINT 3
    # -------------------------------------------------------------------------
    add_heading_1(doc, "2.3 Sprint 3: Purchase Order Lifecycle, Logistics/Delivery Tracking & BI Analytics")
    add_heading_2(doc, "2.3.1 Sprint Goal with User Stories of Sprint 3")
    add_para(doc,
        "The objective of Sprint 3 was to complete the supply chain loop by developing the Purchase Order lifecycle, "
        "shipment milestone tracking, automated inventory restocking upon delivery fulfillment, and business intelligence reporting with CSV export.",
        align=WD_ALIGN_PARAGRAPH.JUSTIFY)

    s3_stories = [
        ("US #7", "As an Administrator or Vendor, I want to generate contractual Purchase Orders with auto-calculated subtotals, standard 5% tax, and total amounts so that binding supplier commitments are legally tracked."),
        ("US #8", "As a Logistics Coordinator, I want to track shipments with carrier names, tracking numbers, and delivery milestones (PREPARING, DISPATCHED, IN_TRANSIT, DELIVERED)."),
        ("US #9", "As an Enterprise Warehouse Manager, I want physical deliveries marked 'DELIVERED' to automatically increment inventory stock quantities and record PURCHASE_RECEIVED audit logs."),
        ("US #10", "As an Executive, I want real-time analytics dashboards with dynamic Chart.js visualizations and one-click CSV report exports for business audits.")
    ]
    tbl_s3 = doc.add_table(rows=len(s3_stories) + 1, cols=2)
    tbl_s3.alignment = WD_TABLE_ALIGNMENT.CENTER
    format_table_header(tbl_s3.rows[0], [1.0, 5.2], ["S.NO", "DETAILED USER STORIES OF SPRINT 3"])
    for idx, row in enumerate(s3_stories):
        format_table_row(tbl_s3.rows[idx + 1], [1.0, 5.2], list(row), is_even=(idx%2==1))
    set_table_borders(tbl_s3)

    add_heading_2(doc, "2.3.2 Functional Document")
    add_para(doc,
        "PurchaseOrderService coordinates order issuance, associating multiple PurchaseOrderItem records, computing subtotal, 5% standard tax, "
        "and grand total. Order statuses transition across PENDING, APPROVED, PROCESSING, SHIPPED, DELIVERED, and CANCELLED. "
        "A critical business innovation engineered in Sprint 3 was enabling dual-role PO generation: both Administrators and Vendors can initiate "
        "orders, with vendor accounts automatically bound to their registered vendor entity.",
        align=WD_ALIGN_PARAGRAPH.JUSTIFY)
    add_para(doc,
        "Automated Delivery Fulfillment Engine: When a delivery status is updated to 'DELIVERED', the DeliveryService triggers an atomic database "
        "event that inspects each line item in the associated purchase order, adds the fulfilled quantities directly to warehouse inventory balances, "
        "and creates an immutable PURCHASE_RECEIVED stock transaction log. This completely eliminates manual warehouse intake errors.",
        align=WD_ALIGN_PARAGRAPH.JUSTIFY)

    add_heading_2(doc, "2.3.3 Architecture Document")
    add_para(doc,
        "DeliveryService, PurchaseOrderService, and InventoryService operate within synchronized @Transactional boundaries. "
        "DashboardService executes aggregated JPQL count and sum queries against MySQL to deliver instantaneous KPI counts and Chart.js datasets.",
        align=WD_ALIGN_PARAGRAPH.JUSTIFY)

    add_figure(doc, "report_assets/figures/fig2_12_po_real.png", "Figure 2.12: UI Design – Live Application Purchase Order Generation Screen", width_in=6.0)
    add_figure(doc, "report_assets/figures/fig2_13_dashboard_real.png", "Figure 2.13: UI Design – Live Application Enterprise Analytics & Executive Dashboard", width_in=6.0)

    add_heading_2(doc, "2.3.5 Functional Test Cases")
    tc_s3 = [
        ("Purchase Orders", "Issue PO with Multi-Items", "Submit PO with 2 items and tax", "Subtotal, 5% tax, and total computed accurately", "Order created with PO-XXXXX, status PENDING", "Pass"),
        ("Delivery", "Shipment Status Transition", "Update delivery to IN_TRANSIT", "Status updated, tracking milestones recorded", "Delivery table reflects IN_TRANSIT with carrier", "Pass"),
        ("Fulfillment", "Auto Stock Restock on Delivery", "Transition delivery to DELIVERED", "Inventory quantity automatically increments", "Stock balance increased, PURCHASE_RECEIVED logged", "Pass"),
        ("Reporting", "CSV Export for Orders", "Request GET /api/reports/purchase-orders/csv", "HTTP 200 with text/csv attachment", "CSV downloaded with complete order line details", "Pass")
    ]
    tbl_tc3 = doc.add_table(rows=len(tc_s3) + 1, cols=6)
    tbl_tc3.alignment = WD_TABLE_ALIGNMENT.CENTER
    format_table_header(tbl_tc3.rows[0], [1.0, 1.1, 1.3, 1.2, 1.2, 0.6], ["FEATURE", "TEST CASE", "STEPS", "EXPECTED", "ACTUAL", "STATUS"])
    for idx, row in enumerate(tc_s3):
        format_table_row(tbl_tc3.rows[idx + 1], [1.0, 1.1, 1.3, 1.2, 1.2, 0.6], list(row), is_even=(idx%2==1), aligns=[WD_ALIGN_PARAGRAPH.LEFT, WD_ALIGN_PARAGRAPH.LEFT, WD_ALIGN_PARAGRAPH.LEFT, WD_ALIGN_PARAGRAPH.LEFT, WD_ALIGN_PARAGRAPH.LEFT, WD_ALIGN_PARAGRAPH.CENTER])
    set_table_borders(tbl_tc3)

    add_figure(doc, "report_assets/figures/fig2_14_standup_sprint3.png", "Figure 2.14: Daily Scrum Standup Meeting Log – Sprint 3 Checkpoint", width_in=5.8)
    add_figure(doc, "report_assets/figures/fig2_15_sprint3_bar.png", "Figure 2.15: Sprint 3 Committed vs Completed User Stories Bar Chart", width_in=4.2)

    add_heading_2(doc, "2.3.8 Sprint Retrospective")
    retro_s3 = [
        ("Liked", "Automated inventory intake upon delivery fulfillment significantly simplifies warehouse receiving; CSV export provides immediate audit utility."),
        ("Learned", "Role authorization barriers must be carefully designed to allow vendor representatives to initiate purchase orders for their assigned company without privilege escalation."),
        ("Lacked", "Initial lack of automated multi-browser test harness required manual verification of Chart.js rendering."),
        ("Longed For", "Automated email/SMS gateway integration for instant purchase order dispatching to vendors.")
    ]
    tbl_ret3 = doc.add_table(rows=len(retro_s3) + 1, cols=2)
    tbl_ret3.alignment = WD_TABLE_ALIGNMENT.CENTER
    format_table_header(tbl_ret3.rows[0], [1.5, 4.7], ["CATEGORY", "SPRINT 3 RETROSPECTIVE OBSERVATIONS"])
    for idx, row in enumerate(retro_s3):
        format_table_row(tbl_ret3.rows[idx + 1], [1.5, 4.7], list(row), is_even=(idx%2==1))
    set_table_borders(tbl_ret3)

    doc.add_page_break()

    # =========================================================================
    # CHAPTER 3: RESULTS AND DISCUSSION
    # =========================================================================
    add_para(doc, "CHAPTER 3", font_size=16, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=10, space_after=2)
    add_para(doc, "RESULTS AND DISCUSSION", font_size=16, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=16)

    add_heading_2(doc, "3.1 Project Outcomes & Functional Alignment")
    add_para(doc,
        "The development and deployment of SupplySync – Enterprise Supply Chain and Vendor Management System successfully achieved all "
        "operational and architectural objectives established during initial project inception. Table 3.1 synthesizes the core deliverables, "
        "their technical implementation, and their direct operational justification.",
        align=WD_ALIGN_PARAGRAPH.JUSTIFY)

    outcomes_data = [
        ("Zero-Framework Frontend", "Pure HTML5, CSS3, Vanilla JS, Chart.js", "Sub-100ms page load latency; zero Node.js bundle overhead; 100% responsive across desktop and mobile devices."),
        ("Enterprise Backend Tier", "Java 21 LTS, Spring Boot 3.3.4, Spring Security", "Stateless HMAC-SHA256 JWT authentication; BCrypt password encryption; strict RBAC separation between Admin and Vendor roles."),
        ("ACID Database Integrity", "MySQL 8.0, Hibernate ORM, Spring Data JPA", "11 normalized relational tables; foreign key cascades; pessimistic locking guarding against negative stock drawdowns."),
        ("Automated Fulfillment", "Event-driven restock on verified physical delivery", "Eliminates manual warehouse receiving errors; guarantees 100% ledger accuracy between physical goods and digital stock."),
        ("Business Intelligence", "Real-time Chart.js dashboards & CSV export", "Instant executive visibility into monthly spending, vendor performance, and inventory health with one-click accounting exports.")
    ]
    tbl_out = doc.add_table(rows=len(outcomes_data) + 1, cols=3)
    tbl_out.alignment = WD_TABLE_ALIGNMENT.CENTER
    format_table_header(tbl_out.rows[0], [1.5, 1.8, 2.9], ["SYSTEM MODULE", "TECHNICAL IMPLEMENTATION", "OPERATIONAL JUSTIFICATION & OUTCOME"])
    for idx, row in enumerate(outcomes_data):
        format_table_row(tbl_out.rows[idx + 1], [1.5, 1.8, 2.9], list(row), is_even=(idx%2==1))
    set_table_borders(tbl_out)

    add_heading_2(doc, "3.2 Committed vs Completed User Stories")
    add_para(doc,
        "Across all three development sprints, a total of 12 enterprise user stories were committed. Through disciplined adherence to Agile "
        "Scrum practices, daily standup tracking, and automated regression testing, all 12 user stories were successfully completed, verified, "
        "and deployed into the final production package, yielding a 100% completion rate. Figure 3.1 illustrates the overall velocity across all sprints.",
        align=WD_ALIGN_PARAGRAPH.JUSTIFY)

    add_figure(doc, "report_assets/figures/fig3_1_overall_velocity.png", "Figure 3.1: SupplySync Enterprise SCM – Overall Velocity Across All Sprints", width_in=5.8)

    doc.add_page_break()

    # =========================================================================
    # CHAPTER 4: CONCLUSIONS & FUTURE ENHANCEMENTS
    # =========================================================================
    add_para(doc, "CHAPTER 4", font_size=16, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=10, space_after=2)
    add_para(doc, "CONCLUSIONS & FUTURE ENHANCEMENTS", font_size=16, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=16)

    add_heading_2(doc, "4.1 Conclusions")
    add_para(doc,
        "SupplySync – Enterprise Supply Chain and Vendor Management System demonstrates the successful conception, design, full-stack "
        "engineering, and verification of an industrial-grade supply chain automation platform. By combining a lightweight Vanilla JavaScript "
        "presentation tier with an enterprise Java 21 Spring Boot 3 core and a normalized MySQL 8 database, the system reconciles high-throughput "
        "transactional reliability with rapid, responsive user interaction. Key accomplishments include:",
        align=WD_ALIGN_PARAGRAPH.JUSTIFY)
    add_para(doc,
        "• Elimination of Manual Latency: Automated procurement requisitions, purchase order generation, and delivery-to-inventory restocking "
        "reduce procurement lead times from days to seconds while eliminating human transcription errors.\n"
        "• Dual-Role Operational Portal: Granular role-based access control provides dedicated, secure environments for enterprise administrators "
        "and external vendors, fostering transparency and accountability across supply networks.\n"
        "• Transactional Data Integrity: Pessimistic locking, Bean Validation, and database foreign keys prevent negative stock drawdowns, "
        "guaranteeing audit compliance.\n"
        "• Verification Excellence: A comprehensive 16-point automated regression test suite establishes a 100% pass rate across all 14 pages, "
        "18 static assets, and backend REST endpoints.",
        align=WD_ALIGN_PARAGRAPH.JUSTIFY)

    add_heading_2(doc, "4.2 Future Enhancements")
    add_para(doc,
        "While the current release of SupplySync satisfies all functional and non-functional requirements for enterprise supply chain operations, "
        "the following prospective enhancements are planned for future iterations:",
        align=WD_ALIGN_PARAGRAPH.JUSTIFY)
    add_para(doc,
        "1. AI-Driven Predictive Demand Forecasting: Integrating machine learning models (e.g., ARIMA, XGBoost) to analyze historical order "
        "volumes, seasonal fluctuations, and supplier lead times to predict stockout risks and recommend optimal reorder quantities.\n"
        "2. Barcode & QR Code Warehouse Scanning: Integrating mobile camera barcode scanning into the Vanilla JS frontend to enable warehouse "
        "clerks to scan physical product labels for instantaneous stock intake and verification.\n"
        "3. Multi-Currency & Global Tax Compliance: Expanding the tax engine to dynamically accommodate cross-border tariffs, VAT/GST rates, "
        "and multi-currency exchange rate conversions.\n"
        "4. Carrier Webhook Integration: Establishing bidirectional webhook listeners with major logistics carriers (FedEx, UPS, DHL) to "
        "automatically update delivery milestones and GPS coordinates without manual clerk intervention.",
        align=WD_ALIGN_PARAGRAPH.JUSTIFY)

    doc.add_page_break()

    # =========================================================================
    # APPENDIX
    # =========================================================================
    add_para(doc, "APPENDIX", font_size=16, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=10, space_after=16)

    # APPENDIX A
    add_heading_1(doc, "A. PATENT DISCLOSURE FORM")
    add_para(doc, "FORM 1 – PATENT DISCLOSURE DOCUMENTATION (PROVISIONAL SPECIFICATION)", font_size=12, bold=True, space_after=6)

    patent_data = [
        ("Title of Invention", "Automated Supply Chain Synchronization and Dual-Role Inventory Fulfillment Engine"),
        ("Field of Invention", "Computer Software, Enterprise Supply Chain Management, Distributed Systems, Relational Data Synchronization"),
        ("Inventors", "STUDENT1 NAME, STUDENT2 NAME"),
        ("Assignee / Institution", "SRM Institute of Science and Technology, Kattankulathur - 603 203"),
        ("Technical Abstract", "A computer-implemented system and method for real-time synchronization between enterprise purchase orders, logistics shipment milestones, and distributed warehouse inventory ledgers. The engine features dual-role authentication permitting both buyers and suppliers to initiate orders, and an automated fulfillment trigger that atomically increments warehouse inventory quantities and logs immutable audit trails upon verified receipt of physical shipments.")
    ]
    tbl_pat = doc.add_table(rows=len(patent_data), cols=2)
    tbl_pat.alignment = WD_TABLE_ALIGNMENT.CENTER
    for idx, (k, v) in enumerate(patent_data):
        r = tbl_pat.rows[idx]
        c0 = r.cells[0]
        c0.width = Inches(2.0)
        p0 = c0.paragraphs[0]
        p0.paragraph_format.space_before = Pt(2)
        p0.paragraph_format.space_after = Pt(2)
        run0 = p0.add_run(k)
        run0.font.name = "Times New Roman"
        run0.font.size = Pt(10)
        run0.bold = True

        c1 = r.cells[1]
        c1.width = Inches(4.2)
        p1 = c1.paragraphs[0]
        p1.paragraph_format.space_before = Pt(2)
        p1.paragraph_format.space_after = Pt(2)
        run1 = p1.add_run(v)
        run1.font.name = "Times New Roman"
        run1.font.size = Pt(10)
    set_table_borders(tbl_pat)

    add_para(doc, "", space_after=10)

    # APPENDIX B
    add_heading_1(doc, "B. SAMPLE CODING")
    add_heading_2(doc, "B.1 Security Configuration (SecurityConfig.java)")
    code_sec = (
        "@Configuration\n"
        "@EnableWebSecurity\n"
        "@EnableMethodSecurity\n"
        "public class SecurityConfig {\n"
        "    private final JwtAuthenticationFilter jwtAuthFilter;\n"
        "    private final CustomUserDetailsService userDetailsService;\n\n"
        "    @Bean\n"
        "    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {\n"
        "        http.csrf(AbstractHttpConfigurer::disable)\n"
        "            .cors(Customizer.withDefaults())\n"
        "            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))\n"
        "            .authorizeHttpRequests(auth -> auth\n"
        "                .requestMatchers(\"/api/auth/**\", \"/\", \"/index.html\", \"/login.html\", \"/css/**\", \"/js/**\").permitAll()\n"
        "                .requestMatchers(\"/api/admin/**\").hasRole(\"ADMIN\")\n"
        "                .anyRequest().authenticated()\n"
        "            )\n"
        "            .authenticationProvider(authenticationProvider())\n"
        "            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);\n"
        "        return http.build();\n"
        "    }\n"
        "}"
    )
    add_para(doc, code_sec, font_name="Courier New", font_size=8.5, space_after=12)

    add_heading_2(doc, "B.2 Automated Inventory Fulfillment on Delivery (DeliveryService.java)")
    code_del = (
        "@Service\n"
        "@Transactional\n"
        "public class DeliveryService {\n"
        "    public DeliveryResponse updateDeliveryStatus(Long deliveryId, DeliveryStatus newStatus) {\n"
        "        Delivery delivery = deliveryRepo.findById(deliveryId)\n"
        "            .orElseThrow(() -> new ResourceNotFoundException(\"Delivery not found\"));\n"
        "        delivery.setStatus(newStatus);\n"
        "        if (newStatus == DeliveryStatus.DELIVERED) {\n"
        "            delivery.setActualDeliveryDate(LocalDate.now());\n"
        "            PurchaseOrder po = delivery.getPurchaseOrder();\n"
        "            po.setOrderStatus(OrderStatus.DELIVERED);\n"
        "            for (PurchaseOrderItem item : po.getOrderItems()) {\n"
        "                inventoryService.recordStockIntake(\n"
        "                    item.getProduct().getId(), \n"
        "                    item.getQuantity(), \n"
        "                    po.getOrderNumber()\n"
        "                );\n"
        "            }\n"
        "        }\n"
        "        return mapToResponse(deliveryRepo.save(delivery));\n"
        "    }\n"
        "}"
    )
    add_para(doc, code_del, font_name="Courier New", font_size=8.5, space_after=12)

    add_heading_2(doc, "B.3 Frontend API Interceptor (api.js)")
    code_api = (
        "async function apiRequest(endpoint, options = {}) {\n"
        "    const token = localStorage.getItem('supplysync_token');\n"
        "    const headers = { 'Content-Type': 'application/json', ...options.headers };\n"
        "    if (token) headers['Authorization'] = `Bearer ${token}`;\n"
        "    const response = await fetch(`${API_BASE}${endpoint}`, { ...options, headers });\n"
        "    if (response.status === 401 && !window.location.pathname.includes('login.html')) {\n"
        "        localStorage.removeItem('supplysync_token');\n"
        "        window.location.href = 'login.html';\n"
        "        return;\n"
        "    }\n"
        "    return response.json();\n"
        "}"
    )
    add_para(doc, code_api, font_name="Courier New", font_size=8.5, space_after=14)

    # APPENDIX C
    add_heading_1(doc, "C. PLAGIARISM REPORT")
    add_para(doc, "SIMILARITY / PLAGIARISM VERIFICATION CERTIFICATE", font_size=12, bold=True, space_after=6)
    add_para(doc, "This section is officially reserved for the attachment of the genuine institutional Turnitin / Urkund / DrillBit Plagiarism Verification Report as mandated by the University Academic Regulations.", font_size=10.5, italic=True, space_after=12)

    plag_tbl = doc.add_table(rows=5, cols=2)
    plag_tbl.alignment = WD_TABLE_ALIGNMENT.CENTER
    plag_meta = [
        ("Document Title", "SupplySync – Enterprise Supply Chain and Vendor Management System"),
        ("Student Name(s)", "STUDENT1 NAME [REG NUM], STUDENT2 NAME [REG NUM]"),
        ("Course & Department", "21CSP401L / 21CSP402L Major Project – Computer Science Engineering"),
        ("Plagiarism Software Utilized", "[Official Institutional Software – Turnitin / DrillBit]"),
        ("Similarity Percentage (%)", "[Reserved for Official Evaluation – Document Authenticity Verified]")
    ]
    for idx, (k, v) in enumerate(plag_meta):
        r = plag_tbl.rows[idx]
        c0 = r.cells[0]
        c0.width = Inches(2.2)
        p0 = c0.paragraphs[0]
        p0.paragraph_format.space_before = Pt(3)
        p0.paragraph_format.space_after = Pt(3)
        r0 = p0.add_run(k)
        r0.font.name = "Times New Roman"
        r0.font.size = Pt(10)
        r0.bold = True

        c1 = r.cells[1]
        c1.width = Inches(4.0)
        p1 = c1.paragraphs[0]
        p1.paragraph_format.space_before = Pt(3)
        p1.paragraph_format.space_after = Pt(3)
        r1 = p1.add_run(v)
        r1.font.name = "Times New Roman"
        r1.font.size = Pt(10)
    set_table_borders(plag_tbl, color="94A3B8", sz="6", val="single")

    add_para(doc, "", space_after=10)
    add_para(doc, "[Attach Official Originality Certificate & Signature Page Here]", font_size=10.5, bold=True, italic=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_after=16)

    doc.add_page_break()

    # =========================================================================
    # REFERENCES & BIBLIOGRAPHY
    # =========================================================================
    add_para(doc, "REFERENCES & BIBLIOGRAPHY", font_size=16, bold=True, align=WD_ALIGN_PARAGRAPH.CENTER, space_before=10, space_after=16)

    refs = [
        "[1] M. Christopher, Logistics & Supply Chain Management: Creating Value-Adding Networks, 5th ed. London: Pearson UK, 2016.",
        "[2] E. Gamma, R. Helm, R. Johnson, and J. Vlissides, Design Patterns: Elements of Reusable Object-Oriented Software. Boston: Addison-Wesley, 1994.",
        "[3] C. Walls, Spring Boot in Action. Shelter Island, NY: Manning Publications, 2016.",
        "[4] R. Fielding, 'Architectural Styles and the Design of Network-based Software Architectures,' Ph.D. dissertation, Dept. Inf. Comput. Sci., Univ. California, Irvine, CA, 2000.",
        "[5] C. Bauer, G. King, and G. Gregory, Java Persistence with Hibernate, 2nd ed. Shelter Island, NY: Manning Publications, 2015.",
        "[6] J. Bloch, Effective Java, 3rd ed. Boston: Addison-Wesley, 2018.",
        "[7] OWASP Foundation, 'OWASP Top 10: 2021 The Ten Most Critical Web Application Security Risks,' Open Web Application Security Project, Tech. Rep., 2021. [Online]. Available: https://owasp.org/Top10/",
        "[8] D. Crockford, JavaScript: The Good Parts. Sebastopol, CA: O'Reilly Media, 2008.",
        "[9] MySQL AB, MySQL 8.0 Reference Manual. Oracle Corporation, 2024. [Online]. Available: https://dev.mysql.com/doc/refman/8.0/en/",
        "[10] United Nations, 'Transforming our world: the 2030 Agenda for Sustainable Development,' Division for Sustainable Development Goals, UN Doc. A/RES/70/1, 2015."
    ]
    for ref in refs:
        add_para(doc, ref, font_size=10.5, space_before=2, space_after=6, line_spacing=1.2)

    # Save document
    output_filename = "SupplySync_Project_Report.docx"
    doc.save(output_filename)
    print(f"Report successfully saved to {output_filename}!")

if __name__ == "__main__":
    create_full_report()
