//
//  Message.h
//  NOS_FirstLab
//
//  Created by Filip Gulan on 17/03/2017.
//  Copyright © 2017 Filip Gulan. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Message : NSObject

@property (nonatomic, assign, readonly) long receiverID;
@property (nonatomic, assign, readonly) long senderID;
@property (nonatomic, strong, readonly) NSString *text;

- (instancetype)initWithReceiver:(long)receiverID senderID:(long)senderID text:(NSString *)text;

@end
