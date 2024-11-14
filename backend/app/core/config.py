from pydantic_settings import BaseSettings


class BaseSettings(BaseSettings):
    APP_NAME: str = "Seeker Drive API"
    API_STR: str = "/api"

    class Config:
        env_file = ".env"


class DevSettings(BaseSettings):
    DEBUG: bool = True
    TESTING: bool = True
    DATABASE_URL: str = "sqlite:///./test.db"
    LOGGING_LEVEL: str = "DEBUG"


class ProdSettings(BaseSettings):
    DEBUG: bool = False
    TESTING: bool = False
    DATABASE_URL: str = "sqlite:///./prod.db"
    LOGGING_LEVEL: str = "INFO"


# 根据传参获取配置
def get_settings(env: str = "dev"):
    if env == "prod":
        return ProdSettings()
    else:
        return DevSettings()
