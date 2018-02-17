//
//  Message.m
//  NOS_FirstLab
//
//  Created by Filip Gulan on 17/03/2017.
//  Copyright © 2017 Filip Gulan. All rights reserved.
//

#import "Message.h"

@interface Message ()

@property (nonatomic, assign) long receiverID;
@property (nonatomic, assign) long senderID;
@property (nonatomic, strong) NSString *text;

@end

@implementation Message

- (instancetype)initWithReceiver:(long)receiverID senderID:(long)senderID text:(NSString *)text
{
    self = [super init];
    if (self) {
        _receiverID = receiverID;
        _senderID = senderID;
        _text = text;
    }
    return self;
}
@end
