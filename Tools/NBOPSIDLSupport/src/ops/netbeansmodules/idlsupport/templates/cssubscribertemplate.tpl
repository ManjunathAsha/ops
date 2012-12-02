//Auto generated OPS-code. !DO NOT MODIFY!

using Ops;

namespace __packageName
{

    public delegate void NewDataEventHandler(__classNameSubscriber sender, __className data);

    public class __classNameSubscriber : Subscriber
    {
        public event NewDataEventHandler newData;

        public __classNameSubscriber(Topic t) : base(t)
        {
            CheckTypeString(__className.typeString);

            //participant.addTypeSupport(__className.getTypeFactory());
        }

        protected override void NewDataArrived(OPSObject o)
        {
            newData(this, (__className)o);
        }

        public __className WaitForNextData(long millis)
        {
            return (__className)WaitForNextOpsObjectData(millis);
        }

        //public __className getData()
        //{
        //    return (__className)base.getData();
        //}
    }

}
