FROM python:3.11-slim

# Set working directory inside the container
WORKDIR /app

# Install dependencies
COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

# Copy source code
COPY . ./

# Expose Flask default port
EXPOSE 5000

# Run the application
CMD ["python", "app.py"]
