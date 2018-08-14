1、proposer ：  1、接收提议请求，然后发起请求给accpptor
2、accpetor:   2、接收proposer发来的请求，然后决定是否接收
3、accpetor(操作是原子的):   3、proposer判断大多数原则，accptotor进行确认决议


1、prepare 编号
2、Promise 提议，如果提议前发现 已经某val被accpt了，则提议赋值 val


3、Accept Request，如果一个议案已经Accept了，那么后续的议案只接收accept的议案
4、




paxos is a family of distribute algo used to reach consensus;

go where to diner?

* 1、consensus is agreeing on one result
* 2、once a majority agrees on a proposal , thar is the consensus
* 3、the reached consensus can be eventually known by everyone
* 4、the involved parties want to agree on any result , not on their proposal
* 5、message may be lost 

* why do system need to reach consensus?
* leader-replica schema
* peer-to-peer schema

* paxos basics 
* roles: proposers : propose value to reach consensus on; acceptors: contribute to reaching the consensus itself ; learners: learn the agreed upon value
* paxos nodes can take multiple roles , even all of them
* paxos nodes must know how many acceptors a majority is 
* a paxos instance aims at reaching a single consensus
* once a consensus is reached , it cannot progress to another consensus
