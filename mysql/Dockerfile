FROM mysql:8.0

# 컨테이너 내부 타임존 설정(한국 시간)
ENV TZ=Asia/Seoul

# 설정 파일 복사 (한글 깨짐 및 환경 설정용)
COPY ./my.cnf /etc/mysql/conf.d/my.cnf

# 컨테이너 최초 생성시 초기 설정 파일 실행
COPY ./init.sql /docker-entrypoint-initdb.d/init.sql

EXPOSE 3306