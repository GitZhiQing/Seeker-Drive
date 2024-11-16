from pydantic_settings import BaseSettings
import os


class SDBaseSettings(BaseSettings):
    APP_NAME: str = "Seeker Drive API"
    API_STR: str = "/api"
    SECRET_KEY: str = "seekerdrive"
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 60 * 24 * 7  # 一周
    # 静态文件目录，默认为项目根目录下的static文件夹
    STATIC_DIR: str = os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(__file__))), "static")
    # 文件存储目录，默认为项目根目录下的drive文件夹
    DRIVE_DIR: str = os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(__file__))), "drive")

    class Config:
        env_file = ".env"
        case_sensitive = True  # 区分大小写


class DevSettingsSD(SDBaseSettings):
    ENV: str = "dev"
    DEBUG: bool = True
    TESTING: bool = True
    DATABASE_URL: str = "sqlite:///./test.db"
    LOGGING_LEVEL: str = "DEBUG"


class ProdSettingsSD(SDBaseSettings):
    ENV: str = "prod"
    DEBUG: bool = False
    TESTING: bool = False
    DATABASE_URL: str = "sqlite:///./prod.db"
    LOGGING_LEVEL: str = "INFO"


# 根据传参获取配置
def get_settings(env: str = "dev"):
    if env == "prod":
        return ProdSettingsSD()
    else:
        return DevSettingsSD()
