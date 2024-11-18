FROM python:3.12.7-slim

# 创建用户和组 app
RUN groupadd -r app && useradd -r -g app app

WORKDIR /code

# 设置 /code 目录的所有者为 app 用户，可写
RUN chown app:app /code && chmod 775 /code

# 复制 requirements.txt 并安装依赖
COPY --chown=app:app ./requirements.txt /code/requirements.txt
RUN pip install --no-cache-dir --upgrade -r /code/requirements.txt -i https://pypi.tuna.tsinghua.edu.cn/simple

# 复制应用代码
COPY --chown=app:app ./app /code/app

# 切换到非 root 用户
USER app

# 设置环境变量
ENV PYTHONUNBUFFERED=1

# 启动应用
CMD ["uvicorn", "app:app", "--host", "0.0.0.0", "--port", "80"]