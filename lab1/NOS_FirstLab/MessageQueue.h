//
//  MessageQueue.h
//  NOS_FirstLab
//
//  Created by Filip Gulan on 17/03/2017.
//  Copyright © 2017 Filip Gulan. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Message.h"

@interface MessageQueue : NSObject

@property (nonatomic, assign, readonly) int queueKey;
@property (nonatomic, assign, readonly) int queueID;

+ (instancetype)new NS_UNAVAILABLE;
- (instancetype)init NS_UNAVAILABLE;

- (instancetype)initWithKey:(int)queueKey;
- (BOOL)sendMessage:(NSString *)text withReceiverID:(int)receiverID andSenderID:(int)senderID;
- (Message *)getMessageForReceiverWithID:(int)receiverID;

@end
