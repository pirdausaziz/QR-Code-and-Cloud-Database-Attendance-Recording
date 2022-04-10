import tkinter as tk
from tkinter import StringVar
import pandas as pd
#import numpy as np

root = tk.Tk()

from firebase_admin import credentials, firestore, _apps, initialize_app
import pyrebase

firebaseConfig = {"** Get your credential on Firebase **"}  
    
    
firebase = pyrebase.initialize_app(firebaseConfig)
auth = firebase.auth()

db_fb = firebase.database()
auth.sign_in_with_email_and_password("dummy@mail.com", "123456")

if not _apps:
    cred = credentials.Certificate('serviceAccountKey.json') 
    default_app = initialize_app(cred)
   
db = firestore.client()


subject_g = ""

def callback_LecID(*args):
    lec_id = clicked_id.get()
    dummy_schedule.destroy()
    
    options2 = [""]
    
    drop_schedules = tk.OptionMenu(root, clicked_schedule, *options2)
    drop_schedules.configure(state="disabled")
    drop_schedules.grid(row=2, column=1)
    
    try:
        
        options2=[]
        subject_code = db.collection('Teacher').document(lec_id).get().to_dict()['subject']
        subj_code.config(text=subject_code)
        
        global subject_g
        subject_g = subject_code
    
        session = db.collection('Subject').document(subject_code).get().to_dict()["session"]
    
        
        # for doc in session:
        #     print(session[0]["date"])
        #     options2.append(doc["date"])
        
        for i in range(len(session)):
            print(session[i]["date"])
            options2.append(f"Session {i+1} (" + session[i]["date"] + ")")
            
        drop_schedules.destroy()
        drop_schedules = tk.OptionMenu(root, clicked_schedule, *options2)
        drop_schedules.configure(state="normal")
        drop_schedules.grid(row=2, column=1)
            
        
        
        
        
    except KeyError:
        print("NOT AVAILABLE")
        # try:
        drop_schedules.configure(state="disabled")
        message.config(text = "            No session detected          ")
        
        root.after(7000, lambda: message.config(text = "Welcome to attendance exporter"))
        # except:
        #     options2 = [""]
        #     drop_schedules = tk.OptionMenu(root, clicked_schedule, *options2)
        #     drop_schedules.configure(state="disable")
        #     drop_schedules.grid(row=2, column=1)
            
        

def get_session(*args):
    global subject_g
    session = clicked_schedule.get()
    session_count = session.split(' ')[1]
    
    session_count = int(session_count) - 1
    
    # df = pd.DataFrame(columns=["ID", "Time", "Temperature", "Status"])
    docs = db.collection("Subject").document(subject_g).get().to_dict()["session"][session_count]
    #print(docs["session"][session_count])
    
    present = docs["present"]
    absent  = docs["absent"]
    
    #datetime = "Date: " + docs["date"] + " Time:" + docs["start_time"] + " " + docs["end_time"]
    date            = docs["date"]
    start_time      = docs["start_time"]
    end_time        = docs["end_time"]
    
    stud_id=[]
    stud_time=[]
    stud_temperature=[]
    stud_status=[]
    
    for i in range(len(present)):
        
        stud_id.append(present[i]["id"])
        stud_time.append(present[i]["time"])
        stud_temperature.append(present[i]["temperature"])
        stud_status.append("present")
        
    for i in range(len(absent)):
        
        stud_id.append(absent[i])
        stud_time.append("NULL")
        stud_temperature.append("NULL")
        stud_status.append("absent")
        
        
    
        
    df = pd.DataFrame({"ID": stud_id, "Time": stud_time, "Temperature": stud_temperature, "Status": stud_status})
    
    # print(datetime)
    print(df)

    
    # df.to_excel("export.xlsx", index=None)
    
    writer = pd.ExcelWriter('export.xlsx', engine='xlsxwriter')
    df.to_excel(writer, sheet_name='Sheet1', startrow = 5, index=False)
    #workbook  = writer.book
    worksheet = writer.sheets['Sheet1']
    # text = datetime
    
    
    worksheet.write(0, 0, "Subject")
    worksheet.write(0, 1, subject_g)
    worksheet.write(1, 0, "Date")
    worksheet.write(1, 1, date)
    worksheet.write(2, 0, "Start Time")
    worksheet.write(2, 1, start_time)
    worksheet.write(3, 0, "End Time")
    worksheet.write(3, 1, end_time)
    writer.save()
    
    message.config(text = "Successfully exported as 'export.xlsx'")
    
    root.after(7000, lambda: message.config(text = "Welcome to attendance exporter"))

        
    

lect_id = tk.Label(root, text="Teacher ID: ")
lect_id.grid(row=0, column=0)

subj_label = tk.Label(root, text="Subject: ")
subj_label.grid(row=1, column=0)

subj_code = tk.Label(root, text="NULL")
subj_code.grid(row=1, column=1)

session_label = tk.Label(root, text="Session: ")
session_label.grid(row=2, column=0)

session_list = tk.Label(root, text="NULL")
session_list.grid(row=2, column=1)



arr=[]
for doc in db.collection('Teacher').stream():
    doc = doc.id
    arr.append(doc)
options1 = arr
options2 = arr




clicked_id = StringVar()
clicked_id.set("NULL")
clicked_schedule = StringVar()
clicked_schedule.set("NULL")

drop_id = tk.OptionMenu(root, clicked_id, *options1)
drop_id.grid(row=0, column=1)

dummy_schedule = tk.OptionMenu(root, clicked_schedule, *options2)
dummy_schedule.configure(state="disabled")
dummy_schedule.grid(row=2, column=1)

message_label = tk.Label(text="Log: ")
message_label.grid(row=3, column=0)

message = tk.Label(text="Welcome to attendance exporter")
message.grid(row=3, column=1)



clicked_id.trace("w", callback_LecID)
clicked_schedule.trace("w", get_session)

# root.grid_rowconfigure(0, minsize=100)
# root.grid_columnconfigure(0, minsize=200)

root.title('i-CAMS attendance exporter')
root.mainloop()