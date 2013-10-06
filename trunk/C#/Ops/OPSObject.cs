///////////////////////////////////////////////////////////
//  OPSObject.cs
//  Implementation of the Class OPSObject
//  Created on:      12-nov-2011 09:25:33
//  Author:
///////////////////////////////////////////////////////////

using System;

namespace Ops 
{
    public class OPSObject : ISerializable 
    {
		protected string key = "k";

        /// Bytes that hold unserialized data for this object.
        /// This happens if a type can not be fully understood by a participants type support.
        public byte[] spareBytes = new byte[0];
		protected string typesString = "";

        public static string GetTypeName()
        {
            return "";  // shall be overriden
        }

        public OPSObject()
        {

        }

		/// 
		/// <param name="type"></param>
		protected void AppendType(string type)
        {
            this.typesString = type + " " + this.typesString;
        }

        /// Returns a new allocated deep copy/clone of this object.
        virtual public object Clone()
        {
            OPSObject cloneResult = new OPSObject();
            FillClone(cloneResult);
            return cloneResult;
		}

		/// 
		/// <param name="cloneResult"></param>
        /// Fills the parameter "cloneResult" with all values from this object.
        virtual public void FillClone(OPSObject cloneResult)
        {
            cloneResult.typesString = this.typesString;
            cloneResult.key = this.key;
            cloneResult.spareBytes = new byte[this.spareBytes.Length];
            this.spareBytes.CopyTo(cloneResult.spareBytes, 0);
        }

        public string GetClassName(bool full)
        {
            string name = this.GetType().ToString();
            if (full)
                return name;
            return name.Substring(1 + name.LastIndexOf('.'));
        }

		public string GetKey()
        {
            return this.key;
        }

		public string GetTypesString()
        {
            return this.typesString;
        }

        internal void SetTypesString(string ts)
        {
            this.typesString = ts;
        }

        /// 
		/// <param name="archive"></param>
        virtual public void Serialize(IArchiverInOut archive)  
        {
            key = archive.Inout("key", key);
        }

		/// 
		/// <param name="key"></param>
		public void SetKey(string key)
        {
            this.key = key;
		}

		virtual public new string ToString()
        {
            return GetClassName(false);
        }

	}

}