//
//  MessageQueue.m
//  NOS_FirstLab
//
//  Created by Filip Gulan on 17/03/2017.
//  Copyright © 2017 Filip Gulan. All rights reserved.
//

#import "MessageQueue.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/msg.h>

typedef struct {
    long receiverID;
    int senderID;
    char text[200];
} MessageStruct;

int _qID;

@interface MessageQueue ()

@property (nonatomic, assign) int queueID;
@property (nonatomic, assign) int queueKey;

@end

@implementation MessageQueue

- (instancetype)initWithKey:(int)queueKey
{
    self = [super init];
    if (self) {
        _queueKey = queueKey;
    }
    self.queueID = msgget(queueKey, 0600 | IPC_CREAT);
    sigset(SIGINT, retreat);
    _qID = self.queueID;

    if (self.queueID == -1) {
        self = nil;
    }
    return self;
}

- (BOOL)sendMessage:(NSString *)text withReceiverID:(int)receiverID andSenderID:(int)senderID
{
    MessageStruct message;
    message.receiverID = receiverID;
    message.senderID = senderID;
    memcpy(message.text, [text UTF8String], text.length + 1);
    if (msgsnd(self.queueID, &message, sizeof(message) - sizeof(long), 0) == -1) {
        return false;
    }
    return true;
}

- (Message *)getMessageForReceiverWithID:(int)receiverID
{
    MessageStruct messageStruct;
    if (msgrcv(self.queueID, (struct msgbuf *)&messageStruct, sizeof(messageStruct) - sizeof(long), receiverID, 0) == -1) {
        return nil;
    }
    Message *message = [[Message alloc] initWithReceiver:messageStruct.receiverID
                                                senderID:messageStruct.senderID
                                                    text:[NSString stringWithFormat:@"%s", messageStruct.text]];
    return message;
}

- (void)dealloc
{
    retreat(0);
}

void retreat(int failure)
{
    if (msgctl(_qID, IPC_RMID, NULL) == -1) {
        exit(1);
    }
    exit(0);
}

@end
