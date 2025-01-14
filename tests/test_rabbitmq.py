#!/usr/bin/env python3
import os
import sys
import json
import time
import pika
from pika.exceptions import AMQPConnectionError, ChannelClosedByBroker

def get_credentials():
    """Get RabbitMQ credentials from environment variables."""
    user = os.getenv('RABBITMQ_USER')
    password = os.getenv('RABBITMQ_PASSWORD')
    
    if not user or not password:
        print("Error: RABBITMQ_USER and RABBITMQ_PASSWORD environment variables must be set")
        sys.exit(1)
    
    return pika.PlainCredentials(user, password)

def get_connection(max_retries=3):
    """Create a connection to RabbitMQ with retry logic."""
    credentials = get_credentials()
    
    for attempt in range(max_retries):
        try:
            connection = pika.BlockingConnection(
                pika.ConnectionParameters(
                    host='localhost',
                    credentials=credentials,
                    connection_attempts=3,
                    retry_delay=2
                )
            )
            print("✓ Successfully connected to RabbitMQ")
            return connection
        except AMQPConnectionError as e:
            if attempt == max_retries - 1:
                print(f"✗ Failed to connect to RabbitMQ after {max_retries} attempts: {e}")
                sys.exit(1)
            print(f"Connection attempt {attempt + 1} failed, retrying...")
            time.sleep(2 ** attempt)  # Exponential backoff

def setup_queue(channel, queue_name):
    """Create queue with required properties if it doesn't exist."""
    try:
        channel.queue_declare(
            queue=queue_name,
            durable=True     # Queue survives broker restart
        )
        print(f"✓ Queue '{queue_name}' created/confirmed")
        return True
    except Exception as e:
        print(f"✗ Failed to setup queue: {e}")
        return False

def send_test_message(channel, queue_name, message):
    """Send a test message to the queue."""
    try:
        channel.basic_publish(
            exchange='',
            routing_key=queue_name,
            body=json.dumps(message),
            properties=pika.BasicProperties(
                delivery_mode=2,  # Make message persistent
                content_type='application/json'
            )
        )
        print(f"✓ Sent message to '{queue_name}': {message}")
        return True
    except Exception as e:
        print(f"✗ Failed to send message: {e}")
        return False

def receive_test_message(channel, queue_name, expected_message):
    """Receive and validate a test message."""
    messages_received = []

    def callback(ch, method, properties, body):
        try:
            received_message = json.loads(body)
            messages_received.append(received_message)
            print(f"✓ Received message: {received_message}")

            # Verify message content
            if received_message == expected_message:
                print("✓ Message content verified")
            else:
                print("✗ Message content mismatch")
                print(f"  Expected: {expected_message}")
                print(f"  Received: {received_message}")

            ch.basic_ack(delivery_tag=method.delivery_tag)
            ch.stop_consuming()
        except json.JSONDecodeError as e:
            print(f"✗ Failed to decode message: {e}")
            ch.basic_nack(delivery_tag=method.delivery_tag)

    try:
        channel.basic_consume(
            queue=queue_name,
            on_message_callback=callback
        )

        print(f" [*] Waiting for messages on '{queue_name}'. Timeout in 10 seconds...")

        # Start consuming with a timeout
        channel.connection.call_later(10, channel.stop_consuming)
        channel.start_consuming()

        return len(messages_received) > 0
    except Exception as e:
        print(f"✗ Error receiving message: {e}")
        return False

def run_tests():
    """Run all RabbitMQ tests."""
    queue_name = 'booking_operations'
    test_message = {
        'operation': 'book_room',
        'room_id': 'A101',
        'guest_id': 'TEST001',
        'timestamp': time.strftime('%Y-%m-%dT%H:%M:%SZ')
    }

    # Step 1: Create connection
    connection = get_connection()
    channel = connection.channel()

    try:
        # Step 2: Setup queue
        if not setup_queue(channel, queue_name):
            sys.exit(1)
        
        # Step 3: Send test message
        if not send_test_message(channel, queue_name, test_message):
            sys.exit(1)
        
        # Step 4: Receive and validate message
        if not receive_test_message(channel, queue_name, test_message):
            sys.exit(1)
        
        print("\n✓ All tests completed successfully")
        
    except Exception as e:
        print(f"\n✗ Test failed with error: {e}")
        sys.exit(1)
    finally:
        try:
            connection.close()
            print("✓ Connection closed properly")
        except Exception as e:
            print(f"✗ Error closing connection: {e}")

if __name__ == "__main__":
    run_tests()