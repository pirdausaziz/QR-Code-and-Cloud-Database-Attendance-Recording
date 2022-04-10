import tkinter as tk
import time, datetime

from firebase_admin import credentials, firestore, _apps, initialize_app
import pyrebase

import pyqrcode
from PIL import ImageTk, Image

from smbus2 import SMBus
from mlx90614 import MLX90614

import RPi.GPIO as GPIO
import time
GPIO.setmode(GPIO.BCM)

import threading
import textwrap
import multithreading

#global declaration
total           = 0
counter_g       = 1
subject_g       = ''
lect_id_g       = ''
end_time_g      = 0.0
today           = ''
timer_g         = 0
one_zero        = 0
schedule_g      = ''
stud_list_g     = []
stud_attend_g   = []
stud_timestp_g  = []
stud_temp_g     = []
temp_g          = 0.0    
current_id_g    = ''
last_id_g       = ''
dist_g          = 0.0

TRIG = 21
ECHO = 20

GPIO.setup(TRIG,GPIO.OUT)
GPIO.setup(ECHO,GPIO.IN)

GPIO.output(TRIG, False)
print ("Waiting For Sensor To Settle")
time.sleep(0.5)


#firebase setup
firebaseConfig = {"** Get yout crediential on Firebase **"}  
    
    
firebase = pyrebase.initialize_app(firebaseConfig)
auth = firebase.auth()
auth.sign_in_with_email_and_password("dummy@mail.com", "123456")
db_fb = firebase.database()

if not _apps:
    cred = credentials.Certificate('serviceAccountKey.json') 
    default_app = initialize_app(cred)
   
db = firestore.client()

class apps_async(multithreading.MultiThread):
    def task(self, task):
        run_async(task)
        
def run_async(task):
    if task==1:
        check_distance()
        
    elif task==2:
        check_id()

async_process = apps_async(threads=2)
async_process.start_threads()
async_process.add_task(1)
async_process.add_task(2)

def check_id():
    global current_id_g, last_id_g, one_zero
    while one_zero==0:
        current_id_g  = db_fb.child('current_id').get().val()
        last_id_g     = db_fb.child('last_id').get().val()

class WelcomePage(tk.Frame):
    def __init__(self, parent, controller):
        tk.Frame.__init__(self, parent)
        self.configure(bg="blue")
        self.controller = controller
        

        canvas= tk.Canvas(self)
        canvas.pack(expand=True, fill='both')
        img= Image.open("/home/pi/Desktop/tkinter/temp/welcome.jpeg")

        resized_image= img.resize((self.controller.screen_width,self.controller.screen_height), Image.ANTIALIAS)
        new_image= ImageTk.PhotoImage(resized_image)

        canvas.create_image(0,0, anchor="nw", image=new_image)
        canvas.photo=new_image


class LoadingPage(tk.Frame):
    def __init__(self, parent, controller):
        tk.Frame.__init__(self, parent)
        self.configure(bg="blue")
        self.controller = controller
        
        self.class_status = tk.Label(self, text="No Class for Today!", font=("Times_New_Roman", 30))
        self.class_status.grid(column=0,row=0,padx=100,pady=200)
    
        
    def update_timer(self):
        global timer_g,one_zero
        updater = self.after(timer_g, self.update_timer)
        if one_zero == 0:
            print("Start")
            one_zero +=1
        else:
            print("Execute task")
            one_zero = 0
            self.after_cancel(updater)
            async_process.add_task(1)
            async_process.add_task(2)
            check_window_status(self.controller,schedule_g)

        
class GeneratorPage(tk.Frame):
    def __init__(self, parent, controller):
        tk.Frame.__init__(self, parent)
        self.configure(bg="red")
        self.controller = controller
        
        self.imageLabel = tk.Label(self)
        self.imageLabel.place(x=150,y=40)
        img = ImageTk.PhotoImage(Image.open("/home/pi/Desktop/tkinter/temp/notfound.jpg"))
        self.imageLabel.config(image=img)
        self.imageLabel.photo = img

        
        self.subject = tk.Label(self, text="Subject: ", font=("Times_New_Roman", 20))

        self.subject.place(x=50,y=380)
    
        self.lect_name = tk.Label(self, text="Lecturer: ", font=("Times_New_Roman", 20))
        self.lect_name.place(x=50,y=430)
        
        # self.update_self()
        
    def update_self(self):
        
        global end_time_g, current_id_g, last_id_g
        self.updater = self.after(1000, self.update_self)
        
        db_fb.child('timestamp').set({".sv": "timestamp"})
        # current_id  = db_fb.child('current_id').get().val()
        # last_id     = db_fb.child('last_id').get().val()
        if current_id_g != last_id_g:
                global subject_g, lect_id_g, counter_g
                name = db.collection('Student').document(f's{current_id_g}').get().to_dict()['name']
                getpage = self.controller.get_page("ScanningPage")
                name = textwrap.wrap(text=name, width=17)
                name = '\n'.join(name)
                getpage.name.config(text=name, anchor='center')
                self.after_cancel(self.updater)
                getpage.get_user_temp()
                self.controller.show_frame(ScanningPage)
                
                counter_g +=1
                db_fb.child('current_index').set(counter_g)
                
                param = [counter_g, subject_g, lect_id_g]
                param = ','.join(map(str,param))
            
                self.controller.generate_QR(param)
                
                
        if end_time_g < time.time():
            print("The class session ended")
            self.after_cancel(self.updater)
            check_window_status(self.controller,schedule_g)

class ScanningPage(tk.Frame):
    def __init__(self, parent, controller):
        tk.Frame.__init__(self, parent)
        self.configure(bg="yellow")
        self.controller = controller
        
        message = tk.Label(self, text="Please scan your temperature!", font=("Times_New_Roman", 30))
        message.grid(column=0,row=0,padx=10,pady=100)
        
        self.name = tk.Label(self, text="NONE", font=("Times_New_Roman", 40), anchor="center")
        self.name.grid(row=1,column=0,padx=20, pady=0)

    def get_user_temp(self):
        global dist_g
        self.updater = self.after(1000, self.get_user_temp)
        dist = dist_g
        if dist<15:
            
            global stud_attend_g, stud_temp_g, stud_timestp_g
            global lect_id_g, subject_g, counter_g, current_id_g
            bus = SMBus(1)
            sensor = MLX90614(bus, address=0x5A)
    #         print ("Ambient Temperature :", sensor.get_ambient())
            print ("Temperature :", sensor.get_object_1())
    #         print("type of data is:", type(sensor.get_object_1()))
            temp = round(sensor.get_object_1(), 2)
            bus.close()
        
            
            stud_attend_g.append(current_id_g)
            stud_temp_g.append(temp)
            stud_timestp_g.append(time.time())
            
            db_fb.child("last_id").set(current_id_g)
            db_fb.child("scanned_id").set(stud_attend_g)
            
            db.collection('Student').document(f's{current_id_g}').set({
                'last_scanned':{
                    'subject':subject_g,
                    'timestamp':datetime.datetime.utcnow(),
                    'temperature':temp}},merge=True)
            
            
            self.after_cancel(self.updater)
            getpage = self.controller.get_page("GeneratorPage")
            getpage.update_self()
            self.controller.show_frame(GeneratorPage)
            
#             getpage = self.controller.get_page("GeneratorPage")
#             getpage.update_self()
# 
#             self.controller.show_frame(GeneratorPage)
            
            
#     def get_temperature(self):
#         bus = SMBus(1)
#         sensor = MLX90614(bus, address=0x5A)
# #         print ("Ambient Temperature :", sensor.get_ambient())
# #         print ("Object Temperature :", sensor.get_object_1())
# #         print("type of data is:", type(sensor.get_object_1()))
#         temp = round(sensor.get_object_1(), 2)
#         bus.close()
#         return temp
#     
#     def write_data(self,temp_val):
#         global stud_attend_g, stud_temp_g, stud_timestp_g
#         global lect_id_g, subject_g, counter_g, current_id_g
#         stud_attend_g.append(current_id_g)
#         stud_temp_g.append(temp_val)
#         stud_timestp_g.append(time.time())
#         
#         db_fb.child("last_id").set(current_id_g)
#         db_fb.child("scanned_id").set(stud_attend_g)
#         
#         db.collection('Student').document(f's{current_id_g}').set({
#             'last_scanned':{
#                 'subject':subject_g,
#                 'timestamp':datetime.datetime.utcnow(),
#                 'temperature':temp_val}},merge=True)
#         
#     def update_qr(self):
#         global lect_id_g, subject_g, counter_g, current_id_g
#         counter_g +=1
#         db_fb.child('current_index').set(counter_g)
#         
#         param = [counter_g, subject_g, lect_id_g]
#         param = ','.join(map(str,param))
#         
#         self.controller.generate_QR(param)
#         getpage = self.controller.get_page("GeneratorPage")
#         getpage.update_self()
#         self.controller.show_frame(GeneratorPage)
        
        
                
#     def update_self(self):
#         global stud_attend_g, stud_temp_g, stud_timestp_g
#         global lect_id_g, subject_g, counter_g, current_id_g
#         temp_val = self.inputTemp.get()
#         stud_attend_g.append(current_id_g)
#         stud_temp_g.append(temp_val)
#         stud_timestp_g.append(time.time())
#         
#         db_fb.child("last_id").set(current_id_g)
#         db_fb.child("scanned_id").set(stud_attend_g)
#         
#         db.collection('Student').document(f's{current_id_g}').set({
#             'last_scanned':{
#                 'subject':subject_g,
#                 'timestamp':datetime.datetime.utcnow()}},merge=True)
#                 
#         counter_g +=1
#         db_fb.child('current_index').set(counter_g)
#         
#         param = [counter_g, subject_g, lect_id_g]
#         param = ','.join(map(str,param))
#         
#         
#         self.controller.generate_QR(param)
#         getpage = self.controller.get_page("GeneratorPage")
#         getpage.update_self()
#         self.controller.show_frame(GeneratorPage)

        
class Application(tk.Tk):
    def __init__(self, *args, **kwargs):
        tk.Tk.__init__(self, *args, **kwargs)
        
        #global window,schedule_g
        
        window = tk.Frame(self)
        window.pack()
        self.screen_width = self.winfo_screenwidth()
        self.screen_height = self.winfo_screenheight()
        window.grid_rowconfigure(0, minsize=self.screen_height)
        window.grid_columnconfigure(0, minsize=self.screen_width)
        
        self.frames = {}
        for F in (LoadingPage, GeneratorPage, ScanningPage, WelcomePage):
            frame = F(window, self)
            self.frames[F] = frame
            frame.grid(row=0, column=0, sticky="nsew")
        
        self.show_frame(WelcomePage)
        self.after(500, self.run_class_status)
        
#         self.aasync = apps_async(threads=2)
#         self.aasync.start_threads()
#         self.aasync.add_task(1)

    
    def run_class_status(self):
        global window,schedule_g
        
        schedule = get_schedule(get_day())
        schedule_g = schedule
        if schedule =="":
            self.show_frame(LoadingPage)
        else:
            check_window_status(self,schedule)
        
    def get_page(self, page_name):
        for page in self.frames.values():
            if str(page.__class__.__name__) == page_name:
                return page
        return None

    def show_frame(self, page):
        Clock()
        frame = self.frames[page]
        frame.tkraise()
        self.title(page.__name__)
        
    def generate_QR(controller, param):
        qr = pyqrcode.create(param)
        photo = tk.BitmapImage(data=qr.xbm(scale=10))
        getpage = controller.get_page("GeneratorPage")
        getpage.imageLabel.config(image=photo)
        getpage.imageLabel.photo = photo
        
def check_window_status(self, schedule):
    available_class = list(filter(lambda x: x['End'] > time.time(), schedule))
    print("Available class: ",available_class)
    if available_class:
        next_class = sorted(available_class, key = lambda i: i['End'])
        print(next_class[0])
        current_class = next_class[0]
        start_time = current_class['Start']
        #end_Time = current_class['End']
        print(start_time)
        
        
        if start_time < time.time():
            get_generator_info(self,current_class)
        else:
            set_next_class(self, current_class)     
   
    else:
        global one_zero
        one_zero == 1
        self.show_frame(LoadingPage)
        getpage = self.get_page("LoadingPage")
        #print(getpage)
        getpage.class_status.config(text="Last class had finished!")
        getpage.class_status.place(x=100, y=100)

def set_next_class(self, current_class):
    start_time = current_class['Start']
    global timer_g
    subject = current_class['Subject']
    
    getpage = self.get_page("LoadingPage")
    print(getpage)
    conv = datetime.datetime.fromtimestamp(start_time)
    textInput = f"Next class: {subject} \n at \n{conv}"
    getpage.class_status.config(text=textInput)
    timer_g = int((start_time-time.time())*1000)
    
    print("The timeer is", timer_g)
    self.show_frame(LoadingPage)
    getpage.update_timer()
    

def get_generator_info(self,current_class):
    global total,counter_g,subject_g,lect_id_g,end_time_g
    
    getpage = self.get_page("GeneratorPage")
    subject = current_class['Subject']
    subject_g = subject
    id_lec = current_class['ID']
    lect_id_g = id_lec
    
    param = [counter_g, subject_g, lect_id_g]
    param = ','.join(map(str,param))
    self.generate_QR(param)
    
    
    print("The subject is: " + subject)
    
    
    getpage.subject.config(text="Subject: " + subject)
#     getpage.subject.place(relx=0.05, rely=0.77)
    db_fb.child('current_subject').set(subject.strip())
    
    
    
    name= db.collection('Teacher').document(f't{id_lec}').get().to_dict()['name']
    getpage.lect_name.config(text="Lecturer: " + name)
#     getpage.lect_name.place(relx=0.05, rely=0.87)
    #print("Name is: " + name)
    
    id_list = db.collection('Subject').document(subject_g).get().to_dict()["student_list"]
    print(id_list)
    db_fb.child('student_list').set(id_list)
    
    #total = len(id_list)
    empty_arr=["0"]
    db_fb.child('scanned_id').set(empty_arr)
    db_fb.child('current_index').set(1)
    db_fb.child('last_id').set("0")
    db_fb.child('current_id').set("0")
    
    
    
    end_time_g = current_class['End']
    
    getpage.update_self()
    self.show_frame(GeneratorPage)
    
        
def get_day():
    
    getDay=datetime.datetime.today().weekday()
    today=""
    
    if(getDay == 0):
    	today = "Monday"
    elif(getDay ==1):
    	today = "Tuesday"
    elif(getDay == 2):
    	today = "Wednesday"
    elif(getDay == 3):
    	today = "Thursday"
    elif(getDay == 4):
    	today = "Friday"
    elif(getDay == 5):
    	today = "Saturday"
    elif(getDay == 6):
    	today = "Sunday"
    	
    print("Today is: ", today)
    
    return today

def get_schedule(today):
    
    docs = db.collection('Schedule').document(today).get()
    result = docs.to_dict()
    print("The result is: ",result)
    
    if result == None:
        return ""
    else:
        today = datetime.date.today()
    
        schedule_list = []
        for timestp in result:
            #print(timestp)
            
            start = result[f'{timestp}']['start']
            
            start = start.split(":")
            start = list(map(int, start))
            start = datetime.datetime(today.year, today.month, today.day,
                                       start[0], start[1])
            #print(start)
            start = start.timestamp()
            #print(start)
            
            end = result[f'{timestp}']['end']
            
            end = end.split(":")
            end = list(map(int, end))
            end = datetime.datetime(today.year, today.month, today.day,
                                       end[0], end[1])
            #print(end)
            end = end.timestamp()
            #print(end)
            name = result[f'{timestp}']['id']
            
            dict_list = {'Subject': timestp, 'Start': start,
                         'End':end, 'ID': name}
            
            schedule_list.append(dict_list)
            
        return schedule_list
    
    

class Clock:
    def __init__(self):
        self.now = time.time()
        self.time1 = ''
        self.time2 = time.strftime('%A %d,%m,%Y %H:%M:%S')
        self.mFrame = tk.Frame()
        # self.mFrame.pack(side="top",expand=True, fill="x")
        self.mFrame.place(x=5, y=0)

        self.watch = tk.Label(self.mFrame, text=self.time2, font=('Times_New_Roman', 21))
        self.watch.pack()

        self.changeLabel()
        
        #self.loop_forever()
        
        #self.after(500, self.loop_forever)
        # async_process.add_task(3)
        
    def loop_forever(self):
        while True:
            print("time elapsed:", self.now-time.time())
            self.time2 = time.strftime('Date: %A %d/%m/%Y Time: %H:%M:%S')
            self.watch.configure(text=self.time2)
            time.sleep(0.5)
        
        
    def changeLabel(self):
        
        self.time2 = time.strftime('Date: %A %d/%m/%Y Time: %H:%M:%S')
        self.watch.configure(text=self.time2)
        self.mFrame.after(100, self.changeLabel)
        
        
def check_distance():
    now = time.time()
    global one_zero
    while one_zero==0:
        global TRIG, ECHO, dist_g
        timeout = time.time() + 0.1
        pulse_start = time.time()
        pulse_end = time.time()
        GPIO.output(TRIG, True)
        time.sleep(0.00001)
        GPIO.output(TRIG, False)

        while GPIO.input(ECHO)==0:
            pulse_start = time.time()
            #print("help")
            if time.time() > timeout:
                break
            
        while GPIO.input(ECHO)==1:
            pulse_end = time.time()
            if time.time() > timeout:
                break
        pulse_duration = pulse_end - pulse_start

        distance = pulse_duration * 17150
        distance = round(distance, 2)
        #print ("Distance:",distance,"cm")
        dist_g = distance
        #print("time elapsed:", now-time.time())
        time.sleep(1)
        
# t = threading.Thread(target=check_distance)
# t.isDaemon()
# t.start()


#.join()

app = Application()
app.attributes("-fullscreen", True)
app.mainloop()
        
            