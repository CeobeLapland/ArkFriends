from gradio_client import Client, handle_file
from pathlib import Path
import sys
print(sys.path)
#import sys
import requests
print(sys.executable)

try:
    response = requests.get("http://127.0.0.1:7860/")
    if response.status_code != 200:
        raise ValueError("服务未准备好")
except Exception as e:
    raise ValueError(f"无法连接到服务: {e}")

import time
time.sleep(5)  # 等待 5 秒

sys.path.append("path_to_gradio_client")
import time

def connect_to_gradio():
    retries = 5
    for i in range(retries):
        try:
            client = Client("http://127.0.0.1:7860/")
            return client
        except ValueError as e:
            print(f"连接失败，重试 {i + 1}/{retries}...")
            time.sleep(2)
    raise ValueError("无法连接到 Gradio 服务")

client = connect_to_gradio()


#client = Client("http://127.0.0.1:7860/")
import gradio as gr

def my_function(input):
    return f"Hello {input}!"

iface = gr.Interface(fn=my_function, inputs="text", outputs="text")
iface.launch(server_name="127.0.0.1", server_port=7860, show_error=True)


#GetVoiceWithRainfallZeroShot
#GetVoiceWithRainfallZeroShot
def GetVoiceWithRainfallZeroShot(input_text, prompt_wav, prompt_text, speed, output_dir, output_file_name, single_file_suffix):
    return client.predict(
        input_text=input_text,
        prompt_wav=prompt_wav,
        prompt_text=prompt_text,
        speed=speed,
        output_dir=output_dir,
        output_file_name=output_file_name,
        single_file_suffix=single_file_suffix,
        api_name="/rainfall_gen_zero_shot"
    )
    '''result = client.predict(
        input_text="注意看，这个男人叫小帅",
        prompt_wav=handle_file('https://github.com/gradio-app/gradio/raw/main/test/test_files/audio_sample.wav'),
        prompt_text="",
        speed=1,
        output_dir="D:/cosyvoice3-rainfall-v2/cosyvoice-rainfall-v2/cosyvoice-rainfall/outputs",
        output_file_name="",
        single_file_suffix="wav",
        api_name="/rainfall_gen_zero_shot"
    )
    print(result)'''




def GetPromptWavRecognition(audio_path):
    return client.predict(
        audio_path=audio_path,
        api_name="/prompt_wav_recognition"
    )
    '''result = client.predict(
        audio_path=handle_file('https://github.com/gradio-app/gradio/raw/main/test/test_files/audio_sample.wav'),
        api_name="/prompt_wav_recognition"
    )
    print(result)'''



def GetVoiceWithRainfallSFT(input_text, sft_dropdown, speed, output_dir, output_file_name, single_file_suffix):
     return client.predict(
        input_text=input_text,
        sft_dropdown=sft_dropdown,
        speed=speed,
        output_dir=output_dir,
        output_file_name=output_file_name,
        single_file_suffix=single_file_suffix,
        api_name="/rainfall_gen_sft"
    )
'''result = client.predict(
    input_text="注意看，这个男人叫小帅",
    sft_dropdown="刻俄柏（中）",
    speed=1,
    output_dir="D:/cosyvoice3-rainfall-v2/cosyvoice-rainfall-v2/cosyvoice-rainfall/outputs",
    output_file_name="",
    single_file_suffix="wav",
    api_name="/rainfall_gen_sft"
)
print(result)'''




def GetVoiceWithRainfallInstruct(input_text, sft_dropdown, instruct_text, speed, output_dir, output_file_name, single_file_suffix):
     return client.predict(
        input_text=input_text,
        sft_dropdown=sft_dropdown,
        instruct_text=instruct_text,
        speed=speed,
        output_dir=output_dir,
        output_file_name=output_file_name,
        single_file_suffix=single_file_suffix,
        api_name="/rainfall_gen_instruct"
    )
'''result = client.predict(
    input_text="注意看，这个男人叫小帅",
    sft_dropdown="刻俄柏（中）",
    instruct_text="湖南话",
    speed=1,
    output_dir="D:/cosyvoice3-rainfall-v2/cosyvoice-rainfall-v2/cosyvoice-rainfall/outputs",
    output_file_name="",
    single_file_suffix="wav",
    api_name="/rainfall_gen_instruct"
)
print(result)'''