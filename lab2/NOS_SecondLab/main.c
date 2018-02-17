//
//  main.m
//  NOS_SecondLab
//
//  Created by Filip Gulan on 19/03/2017.
//  Copyright © 2017 Filip Gulan. All rights reserved.
//

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/wait.h>
#include <sys/types.h>
#include <stdlib.h>
#include <time.h>

#define MAX(a, b) (((a) > (b)) ? (a) : (b))

#define COUNT 3
#define REPLAY 2
#define MAX_LEN 15
#define BUFFER_SIZE 30
#define RESPONSE "Odgovor"
#define REQUEST "Zahtjev"

enum PipeOperation
{
    PipeOperationRead = 0,
    PipeOperationWrite = 1
};

int processID;
int logicClock = 1;

int pipelines[COUNT][COUNT][2];
int response[COUNT];
char receiveBuffer[BUFFER_SIZE];
char sendBuffer[BUFFER_SIZE];

void setupPipes()
{
    for (int idx = 0; idx < COUNT; idx++) {
        for (int idy = 0; idy < COUNT; idy++) {
            if(idx == idy) {
                continue;
            }
            pipe(pipelines[idx][idy]);
        }
    }
}

void sendMessage(int i, int clock, const char* message)
{
    sprintf(sendBuffer, "%s(%d, %02d)", message, processID, clock);
    printf("Proces P%d salje procesu P%d poruku %s\n", processID, i, sendBuffer);
    write(pipelines[processID][i][PipeOperationWrite], sendBuffer, strlen(sendBuffer) + 1);
}

void getMessage(int i, int* otherProcessID, int* otherClock)
{
    read(pipelines[i][processID][PipeOperationRead], receiveBuffer, MAX_LEN);
    sscanf(receiveBuffer, "%*7s(%d, %d)", otherProcessID, otherClock);
    printf("Proces P%d prima poruku %s\n", processID, receiveBuffer);
    logicClock = MAX(logicClock, *otherClock) + 1;
}

void criticalSection()
{
    for (int count = 1; count <= 5; count++) {
        printf("Proces: %d, poziv: %d\n", processID, count);
        logicClock++;
        sleep(1);
    }
}

void sendRequests()
{
    for (int idx = 0; idx < COUNT; idx++) {
        if (idx == processID) {
            continue;
        }
        sendMessage(idx, logicClock, REQUEST);
    }
}

void receiveResposnes()
{
    int otherProcessID, otherProcessClock;
    for (int idx = 0; idx < COUNT; idx++) {
        if (idx == processID) {
            continue;
        }
        getMessage(idx, &otherProcessID, &otherProcessClock);
    }
}

void sendResponses()
{
    for (int idx = 0; idx < COUNT; idx++) {
        if (idx == processID) {
            continue;
        }

        if (response[idx]) {
            sendMessage(idx, response[idx], RESPONSE);
            response[idx] = 0;
        }
    }
}

void receiveRequests()
{
    int otherProcessID, otherProcessClock;
    int currentClock = logicClock;

    for (int idx = 0; idx < COUNT; idx++) {
        if (idx == processID) {
            continue;
        }
        getMessage(idx, &otherProcessID, &otherProcessClock);
        if ((currentClock < otherProcessClock) || (currentClock == otherProcessClock && processID < idx)) {
            response[idx] = otherProcessClock;
        } else {
            sendMessage(idx, otherProcessClock, RESPONSE);
        }
    }
}

void work()
{
    sendRequests();
    receiveRequests();
    receiveResposnes();
    criticalSection();
    sendResponses();
}

void process()
{
    for (int idx = 0; idx < COUNT; idx++) {
        if (idx == processID) {
            continue;
        }
        close(pipelines[processID][idx][PipeOperationRead]);
        close(pipelines[idx][processID][PipeOperationWrite]);
    }
    for (int idx = 0; idx < REPLAY; idx++) {
        work();
    }
}

int main(void)
{
    setupPipes();
    for (int idx = 0; idx < COUNT; idx++) {
        int pid = fork();

        if (pid == -1) {
            printf("Error while forking");
            exit(0);
        } else if (pid == 0) {
            processID = idx;
            process();
            exit(0);
        }
    }

    for (int idx = 0; idx < COUNT;) {
        if (wait(NULL) > 0) {
            idx++;
        }
    }
    exit(0);
}
